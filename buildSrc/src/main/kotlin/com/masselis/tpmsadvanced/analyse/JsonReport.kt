package com.masselis.tpmsadvanced.analyse

import kotlinx.serialization.Serializable

@Serializable
public data class JsonReport(
    val globalObfuscationRate: Fraction,
    val modules: List<Module>,
) {
    @Serializable
    public data class Module(
        val path: String,
        val obfuscationRate: Fraction,
        val watchRules: Set<@Serializable(RegexSerializer::class) Regex>,
        val keptClasses: Set<String>,
    )
}
