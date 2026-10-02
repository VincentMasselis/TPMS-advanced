package com.masselis.tpmsadvanced.data.vehicle.interfaces.impl

import com.masselis.tpmsadvanced.data.vehicle.model.ScannerRecord

internal interface Raw {
    fun asTyre(): ScannerRecord
}
