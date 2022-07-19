package ru.potatophobe.validsl

/**
 * Specifies that validated value must be null
 * */
@Validsl
fun ValueScope<*>.isNull() {
    match { it == null } description "Must be null"
}

/**
 * Specifies that validated value must not be null
 * */
@Validsl
fun ValueScope<*>.notNull() {
    match { it != null } description "Must not be null"
}

/**
 * Specifies that validated value must not be null and be equal to provided value
 *
 * @param value
 * */
@Validsl
fun <T> ValueScope<T>.isEqualTo(value: T & Any) {
    match { it == value } description "Must be equal to $value"
}

/**
 * Specifies that validated value must not be null and be equal to one of provided values
 *
 * @param values
 * */
@Validsl
fun <T> ValueScope<T>.isOneOf(vararg values: T & Any) {
    match { values.contains(it) } description "Must be one of $values"
}

/**
 * Specifies that validated value must not be null and be true
 * */
@Validsl
fun ValueScope<Boolean?>.isTrue() {
    match { it == true } description "Must be true"
}

/**
 * Specifies that validated value must not be null and be false
 * */
@Validsl
fun ValueScope<Boolean?>.isFalse() {
    match { it == false } description "Must be false"
}

/**
 * Specifies that validated value must not be null and be blank char sequence
 * */
@Validsl
fun ValueScope<out CharSequence?>.isBlankCharSequence() {
    match { it?.isBlank() ?: false } description "Must be blank char sequence"
}

/**
 * Specifies that validated value must not be null and be not blank char sequence
 * */
@Validsl
fun ValueScope<out CharSequence?>.notBlankCharSequence() {
    match { it?.isNotBlank() ?: false } description "Must be not blank char sequence"
}

/**
 * Specifies that validated value must not be null and be empty char sequence
 * */
@Validsl
fun ValueScope<out CharSequence?>.isEmptyCharSequence() {
    match { it?.isEmpty() ?: false } description "Must be empty char sequence"
}

/**
 * Specifies that validated value must not be null and be not empty char sequence
 * */
@Validsl
fun ValueScope<out CharSequence?>.notEmptyCharSequence() {
    match { it?.isNotEmpty() ?: false } description "Must be not empty char sequence"
}

/**
 * Specifies that validated value must not be null and be empty collection
 * */
@Validsl
fun ValueScope<out Collection<*>?>.isEmptyCollection() {
    match { it?.isEmpty() ?: false } description "Must be empty collection"
}

/**
 * Specifies that validated value must not be null and be not empty collection
 * */
@Validsl
fun ValueScope<out Collection<*>?>.notEmptyCollection() {
    match { it?.isNotEmpty() ?: false } description "Must be not empty collection"
}

/**
 * Specifies that validated value must not be null and be empty map
 * */
@Validsl
fun ValueScope<out Map<*, *>?>.isEmptyMap() {
    match { it?.isEmpty() ?: false } description "Must be empty map"
}

/**
 * Specifies that validated value must not be null and be not empty map
 * */
@Validsl
fun ValueScope<out Map<*, *>?>.notEmptyMap() {
    match { it?.isNotEmpty() ?: false } description "Must be not empty map"
}

/**
 * Specifies that validated value must have length equal to `length` argument if not null
 *
 * @param length
 * */
@Validsl
fun ValueScope<out CharSequence?>.hasLength(length: Int) {
    match { it?.let { it.length == length } ?: true } description "Length must be $length"
}

/**
 * Specifies that validated value must have length in provided range if not null
 *
 * @param range
 * */
@Validsl
fun ValueScope<out CharSequence?>.hasLengthIn(range: ClosedRange<Int>) {
    match { it?.let { range.contains(it.length) } ?: true } description "Length must be in range $range"
}

/**
 * Specifies that validated value must match provided pattern if not null
 *
 * @param pattern
 * */
@Validsl
fun ValueScope<out CharSequence?>.matchPattern(pattern: String) {
    match { it?.let { it matches Regex(pattern) } ?: true } description "Must match pattern '$pattern'"
}

/**
 * Specifies that validated value must match provided regex if not null
 *
 * @param regex
 * */
@Validsl
fun ValueScope<out CharSequence?>.matchRegex(regex: Regex) {
    match { it?.let { it matches regex } ?: true } description "Must match regex: $regex"
}

/**
 * Specifies that validated value must be greater than provided value if not null
 *
 * @param value
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.greaterThan(value: T & Any) {
    match { it?.let { it > value } ?: true } description "Must be greater than $value"
}

/**
 * Specifies that validated value must be less than provided value if not null
 *
 * @param value
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.lessThan(value: T & Any) {
    match { it?.let { it < value } ?: true } description "Must be less than $value"
}

/**
 * Specifies that validated value must be less or equal to provided value if not null
 *
 * @param maximum
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.maximum(maximum: T & Any) {
    match { it?.let { it <= maximum } ?: true } description "Must be maximum $maximum"
}

/**
 * Specifies that validated value must be greater or equal to provided value if not null
 *
 * @param minimum
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.minimum(minimum: T & Any) {
    match { it?.let { it >= minimum } ?: true } description "Must be minimum $minimum"
}

/**
 * Specifies that validated value must be in provided range if not null
 *
 * @param range
 * */
@Validsl
fun <T : Comparable<T>?> ValueScope<out T>.inRange(range: ClosedRange<T & Any>) {
    match { it?.let { range.contains(it) } ?: true } description "Must be in range $range"
}

/**
 * Specifies that validated value must have size equal to `size` argument if not null
 *
 * @param size
 * */
@Validsl
fun ValueScope<out Collection<*>?>.hasSize(size: Int) {
    match { it?.let { it.size == size } ?: true } description "Size must be $size"
}

/**
 * Specifies that validated value must have size in provided range if not null
 *
 * @param range
 * */
@Validsl
fun ValueScope<out Collection<*>?>.hasSizeIn(range: ClosedRange<Int>) {
    match { it?.let { range.contains(it.size) } ?: true } description "Size must be in range $range"
}
