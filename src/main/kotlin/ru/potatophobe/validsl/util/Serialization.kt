package ru.potatophobe.validsl.util

import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream

internal fun <T : Any> T.write(): ByteArray {
    return ByteArrayOutputStream().use { byteArrayOutputStream ->
        ObjectOutputStream(byteArrayOutputStream).use { objectOutputStream ->
            objectOutputStream.writeObject(this)
        }
        byteArrayOutputStream.toByteArray()
    }
}

internal fun <T : Any> T.writeToString(): String {
    return String(this.write())
}
