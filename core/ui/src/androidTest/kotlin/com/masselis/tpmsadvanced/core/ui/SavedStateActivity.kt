package com.masselis.tpmsadvanced.core.ui

import androidx.appcompat.app.AppCompatActivity
import com.masselis.tpmsadvanced.core.ui.SaveableThreadSafety.NONE
import com.masselis.tpmsadvanced.core.ui.SaveableThreadSafety.SYNCHRONIZED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.lang.Thread.sleep

internal class SavedStateActivity : AppCompatActivity() {
    var booleanSavedStateLambdaCalled = false
    var booleanSavedState by saveable { booleanSavedStateLambdaCalled = true; false }

    var nullableStringSavedState by saveable<String?> { null }

    val concurrentThread = MutableStateFlow(0)
    val heavyDefaultThreadSafe by saveable(threadSafety = SYNCHRONIZED) {
        concurrentThread.update { it + 1 }
        sleep(500)
        concurrentThread.update { it - 1 }
    }
    val heavyDefaultNonThreadSafe by saveable(threadSafety = NONE) {
        concurrentThread.update { it + 1 }
        sleep(500)
        concurrentThread.update { it - 1 }
    }
}