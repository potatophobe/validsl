package ru.potatophobe.validsl

import kotlin.reflect.KProperty1

/**
 * Entry point to default validation implementation
 *
 * @param value value to validate
 * @param validateBlock validation definition
 *
 * @return result of validation
 *
 * @see ValidateScope
 * @see ValidationResult
 * */
@Validsl
fun <T> validate(value: T, validateBlock: ValidateScope<T>.() -> Unit): ValidationResult<T> {
    return ValidateScopeImpl(value, "this").apply(validateBlock).apply()
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

internal class ValidateScopeImpl<T>(
    private val value: T,
    private val path: String
) : ValidateScope<T> {
    private val propertiesScope: PropertiesScope<T> by lazy { PropertiesScopeImpl(value, path) }
    private val valueScope: ValueScope<T> by lazy { ValueScopeImpl(value, path) }
    private val elementsScopes: List<ValidateScope<*>> by lazy {
        (value as Iterable<*>?)?.mapIndexed { i, e -> ValidateScopeImpl(e, "$path[$i]") } ?: listOf()
    }
    private val keysScopes: List<ValidateScope<*>> by lazy {
        (value as Map<*, *>?)?.keys?.mapIndexed { i, e -> ValidateScopeImpl(e, "$path.keys[$i]") } ?: listOf()
    }
    private val valuesScopes: List<ValidateScope<*>> by lazy {
        (value as Map<*, *>?)?.map { (i, e) -> ValidateScopeImpl(e, "$path[$i]") } ?: listOf()
    }

    override fun properties(propertiesBlock: PropertiesScope<T>.() -> Unit) {
        propertiesScope.apply(propertiesBlock)
    }

    override fun value(valueBlock: ValueScope<T>.() -> Unit) {
        valueScope.apply(valueBlock)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E> ValidateScope<out Iterable<E>?>.elements(elementsBlock: ValidateScope<E>.() -> Unit) {
        this@ValidateScopeImpl.elementsScopes.forEach { it.apply(elementsBlock as ValidateScope<*>.() -> Unit) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K> ValidateScope<out Map<K, *>?>.keys(keysBlock: ValidateScope<K>.() -> Unit) {
        this@ValidateScopeImpl.keysScopes.forEach { it.apply(keysBlock as ValidateScope<*>.() -> Unit) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> ValidateScope<out Map<*, V>?>.values(valueBlock: ValidateScope<V>.() -> Unit) {
        this@ValidateScopeImpl.valuesScopes.forEach { it.apply(valueBlock as ValidateScope<*>.() -> Unit) }
    }

    override fun apply(): ValidationResult<T> {
        return mutableListOf<ValidationResult<*>>().apply {
            add(propertiesScope.apply())
            add(valueScope.apply())
            if (value is Iterable<*>) addAll(elementsScopes.map { it.apply() })
            if (value is Map<*, *>) {
                addAll(keysScopes.map { it.apply() })
                addAll(valuesScopes.map { it.apply() })
            }
        }.flatMap { it.faults }.let { ValidationResultImpl(value, it) }
    }
}

internal class PropertiesScopeImpl<T>(
    private val value: T,
    private val path: String
) : PropertiesScope<T> {
    private val validateScopes: MutableList<ValidateScope<*>> = mutableListOf()

    override fun <P> validate(property: KProperty1<T & Any, P>, validateBlock: ValidateScope<P>.() -> Unit) {
        value?.let { validateScopes.add(ValidateScopeImpl(property.get(it), "$path.${property.name}").apply(validateBlock)) }
    }

    override fun apply(): ValidationResult<T> {
        return validateScopes.map { it.apply() }.flatMap { it.faults }.let { ValidationResultImpl(value, it) }
    }
}

internal class ValueScopeImpl<T>(
    private val value: T,
    private val path: String
) : ValueScope<T> {
    private val matchBlocksToDescriptions: MutableMap<(T) -> Boolean, DescriptionDescriptor> = mutableMapOf()

    override fun match(matchBlock: (T) -> Boolean): DescriptionDescriptor {
        return DescriptionDescriptorImpl().also { matchBlocksToDescriptions[matchBlock] = it }
    }

    override fun apply(): ValidationResult<T> {
        return matchBlocksToDescriptions
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
