package com.masselis.tpmsadvanced.feature.androidauto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

internal class Session : Session() {
    override fun onCreateScreen(intent: Intent): Screen = VehicleScreen(carContext)
}