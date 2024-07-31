package com.masselis.tpmsadvanced.core.ui

import androidx.appcompat.app.AppCompatActivity

internal class SavedStateActivity : AppCompatActivity() {
    var defaultLambdaCalled = false
    var booleanSavedState by saveable { defaultLambdaCalled = true; false }
    var nullableStringSavedState by saveable<String?> { null }
}