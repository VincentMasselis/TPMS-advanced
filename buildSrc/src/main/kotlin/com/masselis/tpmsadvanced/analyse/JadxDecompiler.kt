package com.masselis.tpmsadvanced.analyse

import jadx.api.JadxDecompiler
import jadx.api.JavaClass


internal fun JadxDecompiler.filterByPackage(packages: Set<Regex>) =
    filterByPackage(packages) { it.fullName }

internal fun <T : Any> JadxDecompiler.filterByPackage(
    packages: Set<Regex>,
    classTransformation: (JavaClass) -> T
) = classesWithInners
    .filter { `class` ->
        packages.any { watchedPackage -> `class`.`package`.matches(watchedPackage) }
    }
    .mapTo(mutableSetOf(), classTransformation)
    .toSet()