package com.masselis.tpmsadvanced.feature.androidauto.endpoint

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.screen.TabsScreen

internal class Session : Session() {
    override fun onCreateScreen(intent: Intent): Screen = TabsScreen(carContext)
}
