# `Validsl` - type-safe and extensible DSL to validate Kotlin objects

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
                value {
                    hasLengthIn(3..20)
                    matchPattern("<someNamePattern>")
                }
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
                    value {
                        hasLengthIn(3..20)
                        matchPattern("<someNamePattern>")
                    }
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

You can define aliases for frequent constraints

```kotlin
fun <T> ValueScope<T>.isEqualTo(value: T & Any) {
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
```

full list in `src/.../MatchAliases.kt`

### Also, you can validate object `properties`

```kotlin
data class Something(
    val property: String
)

val value: Something // assignment

validate(value) {
    properties { // object properties validation definition
        validate(Something::property) {
            value {
                isEqualTo("something")
            }
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
                value {
                    isEqualTo("something")
                }
            }
        }
    }
} success {
    // do something if validation result is success
} fail {
    // do something if validation result is fail
}
```

### Same for `keys` and `values` if it is a Map

```kotlin
data class Something(
    val propertiesMap: Map<String, String>
)

val value: Something // assignment

validate(value) {
    properties {
        validate(Something::propertiesMap) {
            keys { // object keys validation definition
                value {
                    isEqualTo("something")
                }
            }
            values { // object values validation definition
                value {
                    isEqualTo("something else")
                }
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
