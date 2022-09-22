package ru.potatophobe.validsl

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ValidateScopeImplTest {

    data class Sample(
        val property: String
    )

    @Test
    fun properties() {
        val value = Sample("propertyValue")
        val validationScopeImpl = ValidateScopeImpl<Sample>()

        validationScopeImpl.apply {
            properties {
                validate(Sample::property) {
                    value {
                        match { it == "propertyValue" }
                    }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                assertSame(value, this.value)
            } fail {
                throw Exception()
            }
        }

        validationScopeImpl.apply {
            properties {
                validate(Sample::property) {
                    value {
                        match { it == "anotherValue" }
                    }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                throw Exception()
            } fail {
                assertSame(value, this.value)
                assertEquals("value.property", faults.single().path)
            }
        }
    }

    @Test
    fun value() {
        val value = "someValue"
        val validationScopeImpl = ValidateScopeImpl<String>()

        validationScopeImpl.apply {
            value {
                match { it == "someValue" }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                assertSame(value, this.value)
            } fail {
                throw Exception()
            }
        }

        validationScopeImpl.apply {
            value {
                match { it == "anotherValue" }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                throw Exception()
            } fail {
                assertSame(value, this.value)
                assertEquals("value", faults.single().path)
            }
        }
    }

    @Test
    fun elements() {
        val value = listOf("value1", "value2")
        val validationScopeImpl = ValidateScopeImpl<List<String>>()

        validationScopeImpl.apply {
            elements {
                value {
                    match { it.matches(Regex("value.")) }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                assertSame(value, this.value)
            } fail {
                throw Exception()
            }
        }

        validationScopeImpl.apply {
            elements {
                value {
                    match { it.matches(Regex("value2")) }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                throw Exception()
            } fail {
                assertSame(value, this.value)
                assertEquals("value[0]", faults.single().path)
            }
        }
    }

    @Test
    fun entries() {
        val value = mapOf("key1" to "value", "key2" to "value")
        val validationScopeImpl = ValidateScopeImpl<Map<String, String>>()

        validationScopeImpl.apply {
            entries {
                value {
                    match { it.key != "key1" || it.value == "value" }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                assertSame(value, this.value)
            } fail {
                throw Exception()
            }
        }

        validationScopeImpl.apply {
            entries {
                value {
                    match { it.key != "key1" || it.value == "value1" }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                throw Exception()
            } fail {
                assertSame(value, this.value)
                assertEquals("value.entries[0]", faults.single().path)
            }
        }
    }

    @Test
    fun keys() {
        val value = mapOf("key1" to "value", "key2" to "value")
        val validationScopeImpl = ValidateScopeImpl<Map<String, String>>()

        validationScopeImpl.apply {
            keys {
                value {
                    match { it.matches(Regex("key.")) }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                assertSame(value, this.value)
            } fail {
                throw Exception()
            }
        }

        validationScopeImpl.apply {
            keys {
                value {
                    match { it.matches(Regex("key1")) }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value")success {
                throw Exception()
            } fail {
                assertSame(value, this.value)
                assertEquals("value.keys[1]", faults.single().path)
            }
        }
    }

    @Test
    fun values() {
        val value = mapOf("key1" to "value1", "key2" to "value2")
        val validationScopeImpl = ValidateScopeImpl<Map<String, String>>()

        validationScopeImpl.apply {
            values {
                value {
                    match { it.matches(Regex("value.")) }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                assertSame(value, this.value)
            } fail {
                throw Exception()
            }
        }

        validationScopeImpl.apply {
            values {
                value {
                    match { it.matches(Regex("value2")) }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.applyTo(value, "value") success {
                throw Exception()
            } fail {
                assertSame(value, this.value)
                assertEquals("value[key1]", faults.single().path)
            }
        }
    }
}
