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
        val validationScopeImpl = ValidateScopeImpl(value, "value")

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
            validationScopeImpl.apply() success {
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
            validationScopeImpl.apply() success {
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
        val validationScopeImpl = ValidateScopeImpl(value, "value")

        validationScopeImpl.apply {
            value {
                match { it == "someValue" }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.apply() success {
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
            validationScopeImpl.apply() success {
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
        val validationScopeImpl = ValidateScopeImpl(value, "value")

        validationScopeImpl.apply {
            elements {
                value {
                    match { it.matches(Regex("value.")) }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.apply() success {
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
            validationScopeImpl.apply() success {
                throw Exception()
            } fail {
                assertSame(value, this.value)
                assertEquals("value[0]", faults.single().path)
            }
        }
    }

    @Test
    fun keys() {
        val value = mapOf("key1" to "value", "key2" to "value")
        val validationScopeImpl = ValidateScopeImpl(value, "value")

        validationScopeImpl.apply {
            keys {
                value {
                    match { it.matches(Regex("key.")) }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.apply() success {
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
            validationScopeImpl.apply() success {
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
        val validationScopeImpl = ValidateScopeImpl(value, "value")

        validationScopeImpl.apply {
            values {
                value {
                    match { it.matches(Regex("value.")) }
                }
            }
        }

        assertDoesNotThrow {
            validationScopeImpl.apply() success {
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
            validationScopeImpl.apply() success {
                throw Exception()
            } fail {
                assertSame(value, this.value)
                assertEquals("value[key1]", faults.single().path)
            }
        }
    }
}
