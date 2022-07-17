package com.masselis.tpmsadvanced.core.tools

fun ByteArray.printableHexArray(): String = joinToString(",", "[", "]") {
    String.format("%02x", it)
}