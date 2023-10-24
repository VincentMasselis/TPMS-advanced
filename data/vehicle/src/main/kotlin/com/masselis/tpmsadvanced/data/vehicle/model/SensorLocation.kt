package com.masselis.tpmsadvanced.data.vehicle.model

public enum class SensorLocation(
    internal val byte: UByte,
    public val axle: Axle,
    public val side: Side,
) {
    FRONT_LEFT(0x80u, Axle.FRONT, Side.LEFT),
    FRONT_RIGHT(0x81u, Axle.FRONT, Side.RIGHT),
    REAR_LEFT(0x82u, Axle.REAR, Side.LEFT),
    REAR_RIGHT(0x83u, Axle.REAR, Side.RIGHT);

    public enum class Axle {
        FRONT,
        REAR;
    }

    public enum class Side {
        LEFT,
        RIGHT;
    }
}
