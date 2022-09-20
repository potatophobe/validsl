package ru.potatophobe.validsl

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * Validate given value
 *
 * @param value value to validate
 * @param validateBlock validation definition
 *
 * @return result of validation
 * */
@Validsl
@Suppress("UNCHECKED_CAST")
inline fun <reified T> validate(value: T, noinline validateBlock: ValidateScope<T>.() -> Unit): ValidationResult<T> {
    return validate(value, T::class as KClass<T & Any>, validateBlock)
}

/**
 * Validate given value
 *
 * @param value value to validate
 * @param type validated value type
 * @param validateBlock validation definition
 *
 * @return result of validation
 * */
@Validsl
fun <T> validate(value: T, type: KClass<T & Any>, validateBlock: ValidateScope<T>.() -> Unit): ValidationResult<T> {
    return validation(validateBlock).applyTo(value, type.simpleName!!)
}

/**
 * Create validation that could be applied to some value
 *
 * @param validateBlock validation definition
 *
 * @return applicable validation
 * */
@Validsl
fun <T> validation(validateBlock: ValidateScope<T>.() -> Unit): ValidateScope<T> {
    return ValidateScopeImpl<T>().apply(validateBlock)
}

internal data class ValidationFaultImpl(
    override val path: String,
    override val description: String,
    override val value: Any?
) : ValidationFault

internal class ValidationResultImpl<T>(
    override val value: T,
    override val faults: List<ValidationFault>
) : ValidationResult<T> {
    private val successScope: ValidationResult.SuccessScope<T> by lazy { SuccessScopeImpl(value) }
    private val failScope: ValidationResult.FailScope<T> by lazy { FailScopeImpl(value, faults) }

    override fun success(successBlock: ValidationResult.SuccessScope<T>.() -> Unit) = apply {
        if (faults.isEmpty()) successScope.successBlock()
    }

    override fun fail(failBlock: ValidationResult.FailScope<T>.() -> Unit) = apply {
        if (faults.isNotEmpty()) failScope.failBlock()
    }

    class SuccessScopeImpl<T>(override val value: T) : ValidationResult.SuccessScope<T>

    class FailScopeImpl<T>(override val value: T, override val faults: List<ValidationFault>) : ValidationResult.FailScope<T>
}

internal class ValidateScopeImpl<T> : ValidateScope<T> {
    private val valueScope: ValueScope<T> by lazy { ValueScopeImpl() }
    private val propertiesScope: PropertiesScope<T> by lazy { PropertiesScopeImpl() }
    private val elementsScope: ValidateScope<Any?> by lazy { ValidateScopeImpl() }
    private val entriesScope: ValidateScope<Map.Entry<Any?, Any?>> by lazy { ValidateScopeImpl() }
    private val keysScope: ValidateScope<Any?> by lazy { ValidateScopeImpl() }
    private val valuesScope: ValidateScope<Any?> by lazy { ValidateScopeImpl() }

    override fun value(valueBlock: ValueScope<T>.() -> Unit) {
        valueScope.apply(valueBlock)
    }

    override fun properties(propertiesBlock: PropertiesScope<T>.() -> Unit) {
        propertiesScope.apply(propertiesBlock)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E> ValidateScope<out Iterable<E>?>.elements(elementsBlock: ValidateScope<E>.() -> Unit) {
        this@ValidateScopeImpl.elementsScope.apply(elementsBlock as ValidateScope<Any?>.() -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K, V> ValidateScope<out Map<K, V>?>.entries(entriesBlock: ValidateScope<Map.Entry<K, V>>.() -> Unit) {
        this@ValidateScopeImpl.entriesScope.apply(entriesBlock as ValidateScope<Map.Entry<Any?, Any?>>.() -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K> ValidateScope<out Map<K, *>?>.keys(keysBlock: ValidateScope<K>.() -> Unit) {
        this@ValidateScopeImpl.keysScope.apply(keysBlock as ValidateScope<Any?>.() -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> ValidateScope<out Map<*, V>?>.values(valuesBlock: ValidateScope<V>.() -> Unit) {
        this@ValidateScopeImpl.valuesScope.apply(valuesBlock as ValidateScope<Any?>.() -> Unit)
    }

    override fun applyTo(value: T, path: String): ValidationResult<T> {
        return mutableListOf<ValidationResult<*>>().apply {
            add(propertiesScope.applyTo(value, path))
            add(valueScope.applyTo(value, path))
            if (value is Iterable<*>) addAll(value.mapIndexed { i, e -> elementsScope.applyTo(e, "$path[$i]") })
            if (value is Map<*, *>) {
                addAll(value.entries.mapIndexed { i, e -> entriesScope.applyTo(e, "$path.entries[$i]") })
                addAll(value.keys.mapIndexed { i, e -> keysScope.applyTo(e, "$path.keys[$i]") })
                addAll(value.map { (k, v) -> valuesScope.applyTo(v, "$path[$k]") })
            }
        }.flatMap { it.faults }.let { ValidationResultImpl(value, it) }
    }
}

internal class PropertiesScopeImpl<T> : PropertiesScope<T> {
    private val propertiesToValidations: MutableMap<KProperty1<T & Any, *>, MutableList<ValidateScope<Any?>>> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <P> validate(property: KProperty1<T & Any, P>, validateBlock: ValidateScope<P>.() -> Unit) {
        if (propertiesToValidations[property] == null) {
            propertiesToValidations[property] = mutableListOf()
        }
        propertiesToValidations[property]!!.add(ValidateScopeImpl<P>().apply(validateBlock) as ValidateScope<Any?>)
    }

    override fun applyTo(value: T, path: String): ValidationResult<T> {
        return propertiesToValidations.flatMap { (p, vl) -> vl.map { s -> s.applyTo(value?.let { p.get(it) }, "$path.${p.name}") } }
            .flatMap { it.faults }.let { ValidationResultImpl(value, it) }
    }
}

internal class ValueScopeImpl<T> : ValueScope<T> {
    private val matchBlocksToDescription: MutableMap<(T) -> Boolean, DescriptionDescriptor> = mutableMapOf()

    override fun match(matchBlock: (T) -> Boolean): DescriptionDescriptor {
        return DescriptionDescriptorImpl().also { matchBlocksToDescription[matchBlock] = it }
    }

    override fun applyTo(value: T, path: String): ValidationResult<T> {
        return matchBlocksToDescription
            .filter { (m, _) -> !m(value) }
            .map { (_, d) -> ValidationFaultImpl(path, d.description, value) }
            .let { ValidationResultImpl(value, it) }
    }
}

internal class DescriptionDescriptorImpl : DescriptionDescriptor {
    override var description: String = ""

    override fun description(description: String) {
        this.description = description
    }
}
