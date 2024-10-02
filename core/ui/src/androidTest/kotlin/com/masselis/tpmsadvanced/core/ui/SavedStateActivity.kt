package com.masselis.tpmsadvanced.core.ui

import androidx.appcompat.app.AppCompatActivity
import java.lang.Thread.sleep

internal class SavedStateActivity : AppCompatActivity() {
    var booleanSavedStateLambdaCalled = false
    var booleanSavedState by saveable { booleanSavedStateLambdaCalled = true; false }

    var nullableStringSavedState by saveable<String?> { null }

    val concurrentThreadCount = mutableListOf(0)
    val heavyDefaultThreadSafe by saveable {
        concurrentThreadCount += concurrentThreadCount.last() + 1
        sleep(500)
        concurrentThreadCount += concurrentThreadCount.last() - 1
    }
}