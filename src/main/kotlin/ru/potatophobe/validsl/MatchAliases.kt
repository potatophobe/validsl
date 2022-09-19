package ru.potatophobe.validsl

/*
 * Aliases for more frequent match cases
 *
 * Alias convention:
 *  Aliases starting with `is` specifies that `value` must not be `null`, except `isNull()`
 *      other aliases specifies that `null` `value` will pass validation
 *  All aliases must return `ru.potatophobe.validsl.MatchAlias` object to support multiple aliases on one line using `and`
 *      Example: `{ notNull() and hasLength(10) }`
 * */

/**
 * Utility object to support multiple aliases on one line using `and`
 * */
object MatchAlias

/**
 * Utility function to support multiple aliases on one line
 *
 * Example: `{ notNull() and hasLength(10) }`
 * */
@Validsl
infix fun MatchAlias.and(matchAlias: MatchAlias) = matchAlias

/**
 * Utility function to simplify alias definition
 * */
fun <T> ValueScope<T>.matchAlias(matchBlock: ValueScope<T>.() -> Unit): MatchAlias {
    this.matchBlock()
    return MatchAlias
}


/**
 * Specifies that validated value must be null
 * */
@Validsl
fun ValueScope<*>.isNull() = matchAlias {
    match { it == null } description "Must be null"
}

/**
 * Specifies that validated value must not be null
 * */
@Validsl
fun ValueScope<*>.isNotNull() = matchAlias {
    match { it != null } description "Must not be null"
}

/* Non-nullable matches */

/**
 * Specifies that validated value must not be null and be equal to provided value
 * */
@Validsl
fun <T> ValueScope<T>.isEqualTo(value: T & Any) = matchAlias {
    match { it == value } description "Must be equal to $value"
}

/**
 * Specifies that validated value must not be null and be equal to one of provided values
 * */
@Validsl
fun <T> ValueScope<T>.isOneOf(vararg values: T & Any) = matchAlias {
    match { values.contains(it) } description "Must be one of $values"
}

/**
 * Specifies that validated value must not be null and be true
 * */
@Validsl
fun ValueScope<Boolean?>.isTrue() = matchAlias {
    match { it == true } description "Must be true"
}

/**
 * Specifies that validated value must not be null and be false
 * */
@Validsl
fun ValueScope<Boolean?>.isFalse() = matchAlias {
    match { it == false } description "Must be false"
}

/**
 * Specifies that validated value must not be null and be blank char sequence
 * */
@Validsl
fun ValueScope<out CharSequence?>.isBlankCharSequence() = matchAlias {
    match { it?.isBlank() ?: false } description "Must be blank char sequence"
}

/**
 * Specifies that validated value must not be null and be not blank char sequence
 * */
@Validsl
fun ValueScope<out CharSequence?>.isNotBlankCharSequence() = matchAlias {
    match { it?.isNotBlank() ?: false } description "Must be not blank char sequence"
}

/**
 * Specifies that validated value must not be null and be empty char sequence
 * */
@Validsl
fun ValueScope<out CharSequence?>.isEmptyCharSequence() = matchAlias {
    match { it?.isEmpty() ?: false } description "Must be empty char sequence"
}

/**
 * Specifies that validated value must not be null and be not empty char sequence
 * */
@Validsl
fun ValueScope<out CharSequence?>.isNotEmptyCharSequence() = matchAlias {
    match { it?.isNotEmpty() ?: false } description "Must be not empty char sequence"
}

/**
 * Specifies that validated value must not be null and be empty collection
 * */
@Validsl
fun ValueScope<out Collection<*>?>.isEmptyCollection() = matchAlias {
    match { it?.isEmpty() ?: false } description "Must be empty collection"
}

/**
 * Specifies that validated value must not be null and be not empty collection
 * */
@Validsl
fun ValueScope<out Collection<*>?>.isNotEmptyCollection() = matchAlias {
    match { it?.isNotEmpty() ?: false } description "Must be not empty collection"
}

/**
 * Specifies that validated value must not be null and be empty map
 * */
@Validsl
fun ValueScope<out Map<*, *>?>.isEmptyMap() = matchAlias {
    match { it?.isEmpty() ?: false } description "Must be empty map"
}

/**
 * Specifies that validated value must not be null and be not empty map
 * */
@Validsl
fun ValueScope<out Map<*, *>?>.isNotEmptyMap() = matchAlias {
    match { it?.isNotEmpty() ?: false } description "Must be not empty map"
}

/* Nullable matches */

/**
 * Specifies that validated value must have length equal to `length` argument if not null
 * */
@Validsl
fun ValueScope<out CharSequence?>.hasLength(length: Int) = matchAlias {
    match { it?.let { it.length == length } ?: true } description "Length must be $length"
}

/**
 * Specifies that validated value must have length in provided range if not null
 * */
@Validsl
fun ValueScope<out CharSequence?>.hasLengthIn(range: ClosedRange<Int>) = matchAlias {
    match { it?.let { range.contains(it.length) } ?: true } description "Length must be in range $range"
}

/**
 * Specifies that validated value must match provided pattern if not null
 * */
@Validsl
fun ValueScope<out CharSequence?>.matchPattern(pattern: String) = matchAlias {
    match { it?.let { it matches Regex(pattern) } ?: true } description "Must match pattern '$pattern'"
}

/**
 * Specifies that validated value must match provided regex if not null
 * */
@Validsl
fun ValueScope<out CharSequence?>.matchRegex(regex: Regex) = matchAlias {
    match { it?.let { it matches regex } ?: true } description "Must match regex: $regex"
}

/**
 * Specifies that validated value must be greater than provided value if not null
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.greaterThan(value: T & Any) = matchAlias {
    match { it?.let { it > value } ?: true } description "Must be greater than $value"
}

/**
 * Specifies that validated value must be less than provided value if not null
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.lessThan(value: T & Any) = matchAlias {
    match { it?.let { it < value } ?: true } description "Must be less than $value"
}

/**
 * Specifies that validated value must be less or equal to provided value if not null
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.maximum(maximum: T & Any) = matchAlias {
    match { it?.let { it <= maximum } ?: true } description "Must be maximum $maximum"
}

/**
 * Specifies that validated value must be greater or equal to provided value if not null
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.minimum(minimum: T & Any) = matchAlias {
    match { it?.let { it >= minimum } ?: true } description "Must be minimum $minimum"
}

/**
 * Specifies that validated value must be in provided range if not null
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.inRange(range: ClosedRange<T & Any>) = matchAlias {
    match { it?.let { range.contains(it) } ?: true } description "Must be in range $range"
}

/**
 * Specifies that validated value must have size equal to `size` argument if not null
 * */
@Validsl
fun ValueScope<out Collection<*>?>.hasSize(size: Int) = matchAlias {
    match { it?.let { it.size == size } ?: true } description "Size must be $size"
}

/**
 * Specifies that validated value must have size in provided range if not null
 * */
@Validsl
fun ValueScope<out Collection<*>?>.hasSizeIn(range: ClosedRange<Int>) = matchAlias {
    match { it?.let { range.contains(it.size) } ?: true } description "Size must be in range $range"
}

/**
 * Specifies that provided key must be mapped only to provided value
 * */
@Validsl
fun <K, V> ValueScope<out Map.Entry<K, V>?>.mapsKey(mapping: Pair<K, V>) = matchAlias {
    match {
        it?.let { it.key != mapping.first || it.value == mapping.second } ?: true
    } description "Key ${mapping.first} must be mapped to ${mapping.second}"
}

/**
 * Specifies that provided value must be mapped only to provided key
 * */
@Validsl
fun <K, V> ValueScope<out Map.Entry<K, V>?>.mapsValue(mapping: Pair<V, K>) = matchAlias {
    match {
        it?.let { it.value != mapping.first || it.key == mapping.second } ?: true
    } description "Value ${mapping.first} must be mapped to ${mapping.second}"
}

/**
 * Specifies that provided first must be mapped only to provided second
 * */
@Validsl
fun <A, B> ValueScope<Pair<A, B>?>.mapsFirst(mapping: Pair<A, B>) = matchAlias {
    match {
        it?.let { it.first != mapping.first || it.second == mapping.second } ?: true
    } description "First ${mapping.first} must be mapped to ${mapping.second}"
}

/**
 * Specifies that provided second must be mapped only to provided first
 * */
@Validsl
fun <A, B> ValueScope<Pair<A, B>?>.mapsSecond(mapping: Pair<B, A>) = matchAlias {
    match {
        it?.let { it.second != mapping.first || it.first == mapping.second } ?: true
    } description "Second ${mapping.first} must be mapped to ${mapping.second}"
}
