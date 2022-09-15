package com.masselis.tpmsadvanced.data.record.model

public enum class TyreLocation(
    internal val byte: UByte
) {
    FRONT_LEFT(0x80u),
    FRONT_RIGHT(0x81u),
    REAR_LEFT(0x82u),
    REAR_RIGHT(0x83u);
}
