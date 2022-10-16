# `Validsl` - type-safe and extensible DSL to validate Kotlin objects

[![Maven Central](https://img.shields.io/badge/Maven%20Central-grey)](https://search.maven.org/search?q=g:ru.potatophobe%20a:validsl)



## Getting started

`Gradle`

```kotlin
dependencies {
    implementation("ru.potatophobe:validsl:$validslVersion")
}
```

---
`Maven`

```xml

<dependencies>
    <dependency>
        <groupId>ru.potatophobe</groupId>
        <artifactId>validsl</artifactId>
        <version>${validslVersion}</version>
    </dependency>
</dependencies>
```

---

### Main purpose is validation depending on context

```kotlin
fun processPerson(person: Person?) {
    validate(person) {
        value { notNull() }
        properties {
            validate(Person::name) {
                value { hasLengthIn(3..20) and matchPattern("<someNamePattern>") }
            }
            validate(Person::phoneNumbers) {
                value { notEmptyCollection() }
                elements {
                    value { matchPattern("<somePhonePattern>") }
                }
            }
            validate(Person::familyMap) {
                value { notEmptyMap() }
                keys {
                    value { isOneOf("Mother", "Father", "Sister", "Brother") }
                }
                values {
                    value { matchPattern("<someNamePattern>") }
                }
            }
        }
    } fail {
        throw BusinessException(value, faults)
    }

    // main logic
}
```

### But you can use it as default context independent validation

```kotlin
data class Person(
    val name: String,
    val phoneNumbers: List<String>,
    val familyMap: Map<String, String>
) {
    init {
        validate(this) {
            value { notNull() }
            properties {
                validate(Person::name) {
                    value { hasLengthIn(3..20) and matchPattern("<someNamePattern>") }
                }
                validate(Person::phoneNumbers) {
                    value { notEmptyCollection() }
                    elements {
                        value { matchPattern("<somePhonePattern>") }
                    }
                }
                validate(Person::familyMap) {
                    value { notEmptyMap() }
                    keys {
                        value { isOneOf("Mother", "Father", "Sister", "Brother") }
                    }
                    values {
                        value { matchPattern("<someNamePattern>") }
                    }
                }
            }
        } fail {
            throw BusinessException(value, faults)
        }
    }
}
```

### Base validation code looks like

```kotlin
val value: String // assignment

validate(value) {
    value {
        match { it == "something" } description "Must be equal to something"
    }
} success {
    // do something if validation result is success
} fail {
    // do something if validation result is fail
}
```

If value not equal to `"something"` `fail` block will execute.
Inside `fail` block you have access to `value` property that contains actual validated value,
and `faults` property that is list of validation faults with 3 properties:
`path` - path to property, which failed validation,
`description` - constraint description you defined above,
`value` - actual value of property, which failed validation

### Already simple and beautiful, but could be better

You can define aliases for frequent constraints according to convention:

    Aliases starting with `is` specifies that `value` must not be `null`, except `isNull()`
        other aliases specifies that `null` `value` will pass validation
    All aliases must return `ru.potatophobe.validsl.MatchAlias` object to support multiple aliases on one line using `and`
        Example: `{ notNull() and hasLength(10) }`

```kotlin
fun <T> ValueScope<T>.isEqualTo(value: T & Any) = matchAlias {
    match { it == value } description "Must be equal to $value"
}
```

and now validation code looks better

```kotlin
val value: String // assignment

validate(value) {
    value {
        isEqualTo("something")
    }
} success {
    // do something if validation result is success
} fail {
    // do something if validation result is fail
}
```

Most frequent constraints already have aliases

```kotlin
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
```

full list in `ru.potatophobe.validsl.MatchAliases.kt`

### Also, you can validate object `properties`

```kotlin
data class Something(
    val property: String
)

val value: Something // assignment

validate(value) {
    properties { // object properties validation definition
        validate(Something::property) {
            value { isEqualTo("something") }
        }
    }
} success {
    // do something if validation result is success
} fail {
    // do something if validation result is fail
}
```

### And `elements` if it is `Iterable`

```kotlin
data class Something(
    val properties: List<String>
)

val value: Something // assignment

validate(value) {
    properties {
        validate(Something::properties) {
            elements { // object elements validation definition
                value { isEqualTo("something") }
            }
        }
    }
} success {
    // do something if validation result is success
} fail {
    // do something if validation result is fail
}
```

### Same for `entries`, `keys` and `values` if it is a Map

```kotlin
data class Something(
    val propertiesMap: Map<String, String>
)

val value: Something // assignment

validate(value) {
    properties {
        validate(Something::propertiesMap) {
            entries {
                value { mapsKey("key" to "value") }
            }
            keys { // object keys validation definition
                value { isEqualTo("something") }
            }
            values { // object values validation definition
                value { isEqualTo("something else") }
            }
        }
    }
} success {
    // do something if validation result is success
} fail {
    // do something if validation result is fail
}
```

### Try it yourself
