package com.masselis.tpmsadvanced.qrcode.interfaces

import android.content.Context
import androidx.startup.Initializer
import com.masselis.tpmsadvanced.interfaces.CoreInitializer
import com.masselis.tpmsadvanced.interfaces.coreComponent
import com.masselis.tpmsadvanced.qrcode.ioc.DaggerQrCodeComponent
import com.masselis.tpmsadvanced.qrcode.ioc.QrCodeComponent

private lateinit var privateComponent: QrCodeComponent
val qrCodeComponent get() = privateComponent

class QrCodeInitializer : Initializer<QrCodeComponent> {
    override fun create(context: Context): QrCodeComponent = DaggerQrCodeComponent
        .factory()
        .build(coreComponent)
        .also { privateComponent = it }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreInitializer::class.java
    )

}