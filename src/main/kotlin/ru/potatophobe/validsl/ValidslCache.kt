package ru.potatophobe.validsl

import kotlin.reflect.KClass

/**
 * Validate given value using cache
 *
 * @param value value to validate
 * @param validateBlock validation definition
 *
 * @return result of validation
 * */
@Validsl
@Suppress("UNCHECKED_CAST")
inline fun <reified T> validateCaching(value: T, noinline validateBlock: ValidateScope<T>.() -> Unit): ValidationResult<T> {
    return validateCaching(value, T::class as KClass<T & Any>, validateBlock)
}

/**
 * Validate given value using cache
 *
 * @param value value to validate
 * @param type validated value type
 * @param validateBlock validation definition
 *
 * @return result of validation
 * */
@Validsl
fun <T> validateCaching(value: T, type: KClass<T & Any>, validateBlock: ValidateScope<T>.() -> Unit): ValidationResult<T> {
    return validationCaching(validateBlock).applyTo(value, type.simpleName!!)
}

/**
 * Create validation that could be applied to some value or return cached
 *
 * @param validateBlock validation definition
 *
 * @return applicable validation
 * */
@Validsl
fun <T> validationCaching(validateBlock: ValidateScope<T>.() -> Unit): ValidateScope<T> {
    return ValidslCache.getValidation(validateBlock)
}

internal object ValidslCache {
    private val validateBlocksToValidation: MutableMap<ValidateScope<*>.() -> Unit, ValidateScope<*>> by lazy { mutableMapOf() }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValidation(validateBlock: ValidateScope<T>.() -> Unit): ValidateScope<T> {
        return if (validateBlock in validateBlocksToValidation) validateBlocksToValidation[validateBlock] as ValidateScope<T>
        else validation(validateBlock).also { validateBlocksToValidation[validateBlock as ValidateScope<*>.() -> Unit] = it }
    }
}
