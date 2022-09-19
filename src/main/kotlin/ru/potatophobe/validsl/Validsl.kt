package ru.potatophobe.validsl

import kotlin.reflect.KProperty1

/**
 * Marks Validsl API components
 * */
@DslMarker
internal annotation class Validsl

/**
 * Describes validation fault
 * */
interface ValidationFault {
    /**
     * Path to property, which failed validation
     *
     * Example: `ClassName.propertyName.propertyName`
     * */
    val path: String

    /**
     * Validation constraint description
     *
     * Example: `Must not be null`
     * */
    val description: String

    /**
     * Value of property, which failed validation
     * */
    val value: Any?
}

/**
 * Describes validation result
 * */
@Validsl
interface ValidationResult<T> {
    /**
     * Validated value
     * */
    val value: T

    /**
     * List of validation faults, if any
     * */
    val faults: List<ValidationFault>

    /**
     * Specifies behavior if there are no faults
     *
     * @param successBlock block of code to be executed if there are no faults
     *
     * @return this
     * */
    @Validsl
    infix fun success(successBlock: SuccessScope<T>.() -> Unit): ValidationResult<T>

    /**
     * Specifies behavior if there are faults
     *
     * @param failBlock block of code to be executed if there are faults
     *
     * @return this
     * */
    @Validsl
    infix fun fail(failBlock: FailScope<T>.() -> Unit): ValidationResult<T>

    /**
     * Provides access to validated value if there are no faults
     * */
    @Validsl
    interface SuccessScope<T> {
        /**
         * Validated value
         * */
        val value: T
    }

    /**
     * Provides access to validated value and validation faults if any
     * */
    @Validsl
    interface FailScope<T> {
        /**
         * Validated value
         * */
        val value: T

        /**
         * List of validation faults
         * */
        val faults: List<ValidationFault>
    }
}

/**
 * Validation unit that could be applied
 * */
interface ApplicableValidation<T> {
    /**
     * Apply validation unit to validated value
     *
     * @return result of validation
     * */
    fun apply(): ValidationResult<T>
}

/**
 * Defines validation for provided value
 *
 * @see validate
 * */
@Validsl
interface ValidateScope<T> : ApplicableValidation<T> {
    /**
     * Specifies validation for provided value
     *
     * @param valueBlock validation definition
     * */
    @Validsl
    fun value(valueBlock: ValueScope<T>.() -> Unit)

    /**
     * Specifies validation for value properties
     *
     * @param propertiesBlock validation definition
     * */
    @Validsl
    fun properties(propertiesBlock: PropertiesScope<T>.() -> Unit)

    /**
     * Specifies validation for value elements if it is Iterable
     *
     * @param elementsBlock validation definition
     * */
    @Validsl
    fun <E> ValidateScope<out Iterable<E>?>.elements(elementsBlock: ValidateScope<E>.() -> Unit)

    /**
     * Specifies validation for value entries if it is Map
     *
     * @param entriesBlock validation definition
     * */
    @Validsl
    fun <K, V> ValidateScope<out Map<K, V>?>.entries(entriesBlock: ValidateScope<Map.Entry<K, V>>.() -> Unit)

    /**
     * Specifies validation for value keys if it is Map
     *
     * @param keysBlock validation definition
     * */
    @Validsl
    fun <K> ValidateScope<out Map<K, *>?>.keys(keysBlock: ValidateScope<K>.() -> Unit)

    /**
     * Specifies validation for value values if it is Map
     *
     * @param valuesBlock validation definition
     * */
    @Validsl
    fun <V> ValidateScope<out Map<*, V>?>.values(valuesBlock: ValidateScope<V>.() -> Unit)
}

/**
 * Defines validation for provided value members
 * */
@Validsl
interface PropertiesScope<T> : ApplicableValidation<T> {
    /**
     * Specifies validation for provided value property
     *
     * @param property property to validate
     * @param validateBlock validation definition
     * */
    @Validsl
    fun <P> validate(property: KProperty1<T & Any, P>, validateBlock: ValidateScope<P>.() -> Unit)
}

/**
 * Defines validation for provided value
 * */
@Validsl
interface ValueScope<T> : ApplicableValidation<T> {
    /**
     * Add validation constraint for provided value
     *
     * @param matchBlock constraint predicate
     * */
    @Validsl
    fun match(matchBlock: (T) -> Boolean): DescriptionDescriptor
}

/**
 * Defines description for specific constraint
 * */
@Validsl
interface DescriptionDescriptor {
    /**
     * Defined description
     *
     * Example: `Must not be null`
     * */
    val description: String

    /**
     * Specifies description for constraint
     * */
    @Validsl
    infix fun description(description: String)
}
