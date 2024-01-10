package com.masselis.tpmsadvanced.interfaces.composable

import java.util.UUID

@Suppress("MemberVisibilityCanBePrivate")
internal sealed interface Path {

    @JvmInline
    value class Home(val vehicleUUID: UUID) : Path {
        override fun toString(): String = "vehicle/$vehicleUUID/home"
    }

    @JvmInline
    value class Settings(val vehicleUUID: UUID) : Path {
        override fun toString(): String = "vehicle/$vehicleUUID/settings"
    }

    @JvmInline
    value class BindingMethod(val vehicleUUID: UUID) : Path {
        override fun toString(): String = "vehicle/$vehicleUUID/binding_method"
    }

    @JvmInline
    value class QrCode(val vehicleUUID: UUID) : Path {
        override fun toString(): String = "vehicle/$vehicleUUID/qrcode"
    }

    @JvmInline
    value class Unlocated(val vehicleUUID: UUID) : Path {
        override fun toString(): String = "vehicle/$vehicleUUID/unlocated"
    }

    companion object {
        @Suppress("NAME_SHADOWING")
        fun from(route: String): Path = route
            .split('/')
            .let { (host, uuid, screen) ->
                assert(host == "vehicle")
                val uuid = UUID.fromString(uuid)
                when (screen) {
                    "home" -> Home(uuid)
                    "settings" -> Settings(uuid)
                    "binding_method" -> BindingMethod(uuid)
                    "qrcode" -> QrCode(uuid)
                    "unlocated" -> Unlocated(uuid)
                    else -> error("Unrecognized route: \"$route\"")
                }
            }
    }
}
