package com.masselis.tpmsadvanced.tools

fun ByteArray.printableHexArray(): String = joinToString(",", "[", "]") {
    String.format("%02x", it)
}