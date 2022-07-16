package com.masselis.tpmsadvanced.qrcode.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.interfaces.StartInitializer
import com.masselis.tpmsadvanced.qrcode.ioc.QrCodeComponent

private lateinit var privateComponent: QrCodeComponent
val qrCodeComponent get() = privateComponent

class QrCodeInitializer : Initializer<QrCodeComponent> {
    override fun create(context: Context): QrCodeComponent {
        TODO("Not yet implemented")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        StartInitializer::class.java
    )

}