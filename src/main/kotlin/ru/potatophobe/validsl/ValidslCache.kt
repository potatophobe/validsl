package ru.potatophobe.validsl

import ru.potatophobe.validsl.util.writeToString

/**
 * Validate given value using cached validation
 *
 * @param value value to validate
 * @param validateBlock validation definition
 *
 * @return result of validation
 * */
@Validsl
fun <T> validateCaching(value: T, validateBlock: ValidateScope<T>.() -> Unit): ValidationResult<T> {
    return ValidslCache.cachedValidation(validateBlock).applyTo(value, "this")
}

internal object ValidslCache {
    private val cache: MutableMap<String, ValidateScope<*>> by lazy { mutableMapOf() }

    @Suppress("UNCHECKED_CAST")
    fun <T> cachedValidation(validateBlock: ValidateScope<T>.() -> Unit): ValidateScope<T> {
        val key = validateBlock.writeToString()
        return if (key in cache) cache[key] as ValidateScope<T>
        else validation(validateBlock).also { cache[key] = it }
    }
}
