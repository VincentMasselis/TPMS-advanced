package com.masselis.tpmsadvanced.qrcode.usecase

import android.Manifest.permission.CAMERA
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.qrcode.model.SensorMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors
import javax.inject.Inject

internal class QrCodeAnalyserUseCase @Inject constructor() {

    private val scanner = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
        .let { BarcodeScanning.getClient(it) }
    private val executor = Executors.newCachedThreadPool()

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
    @Suppress("MagicNumber", "CyclomaticComplexMethod")
    fun analyse(controller: CameraController): Flow<SensorMap> = callbackFlow<List<Barcode>> {
        controller.setImageAnalysisAnalyzer(
            executor,
            MlKitAnalyzer(
                listOf(scanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                executor
            ) {
                it.getValue(scanner)?.also { barcodes -> launch { send(barcodes) } }
            }
        )
        awaitClose { controller.clearImageAnalysisAnalyzer() }
    }.flowOn(Dispatchers.Main.immediate)
        .flatMapConcat { it.asFlow() }
        .filter { it.valueType == Barcode.TYPE_TEXT }
        .mapNotNull { it.rawValue }
        .mapNotNull {
            fourSensorRegex.find(it)?.groupValues?.subList(1, 5)
                ?: twoSensorRegex.find(it)?.groupValues?.subList(1, 3)
        }
        .map { stringHexs ->
            stringHexs
                .map { stringHex ->
                    Pair(
                        // Trying to recognize the location with the id of the sensor
                        when (stringHex.first()) {
                            '1' -> FRONT_LEFT
                            '2' -> FRONT_RIGHT
                            '3' -> REAR_LEFT
                            '4' -> REAR_RIGHT
                            else -> null
                        },
                        // Converts the hexadecimal id to an int
                        stringHex
                            .hexToByteArray()
                            .let {
                                ByteBuffer
                                    .wrap(byteArrayOf(0x00) + it)
                                    .order(ByteOrder.LITTLE_ENDIAN)
                                    .int
                            },
                    )
                }
                .mapIndexed { index, (location, id) ->
                    Sensor(
                        id,
                        // The sensor id didn't provided the location, let's determine it with the
                        // list index
                        location ?: when (index) {
                            0 -> FRONT_LEFT
                            1 -> FRONT_RIGHT
                            2 -> REAR_LEFT
                            3 -> REAR_RIGHT
                            else -> error("Filled list cannot have more than 4 entries")
                        }
                    )
                }
                .distinctBy { (_, location) -> location }
                .let(::SensorMap)
        }
        .flowOn(Dispatchers.Default)

    fun requiredPermission() = CAMERA

    companion object {
        // Test available here: https://regex101.com/r/c2xslp/1
        private val fourSensorRegex =
            "([0-9a-fA-F]{6})&([0-9a-fA-F]{6})&([0-9a-fA-F]{6})&([0-9a-fA-F]{6})".toRegex()

        private val twoSensorRegex =
            "([0-9a-fA-F]{6})&([0-9a-fA-F]{6})".toRegex()
    }
}
