package ru.potatophobe.validsl

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertSame

class ValidationResultImplTest {

    @Test
    fun success() {
        val value = "value"
        val validationResultImpl = ValidationResultImpl(value, listOf())

        assertDoesNotThrow {
            validationResultImpl success {
                assertSame(value, this.value)
            } fail {
                throw Exception()
            }
        }
    }
    
    @Test
    fun fail() {
        val value = "value"
        val validationFaults = listOf(ValidationFaultImpl("value", "", value))
        val validationResultImpl = ValidationResultImpl(value, validationFaults)

        assertDoesNotThrow {
            validationResultImpl success {
                throw Exception()
            } fail {
                assertSame(value, this.value)
                assertSame(validationFaults, this.faults)
            }
        }
    }
}