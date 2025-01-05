package com.masselis.tpmsadvanced

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import com.masselis.tpmsadvanced.AutomotiveComponent.Companion.TpmsScreenFactory

public class TpmsCarAppSession(info: SessionInfo): Session() {

    private lateinit var mTpmsScreen: TpmsScreen


    override fun onCreateScreen(intent: Intent): Screen {
        mTpmsScreen = TpmsScreenFactory(carContext)
        return mTpmsScreen
    }
}