package com.masselis.tpmsadvanced.qrcode.usecase

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.masselis.tpmsadvanced.data.car.model.Sensor
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.qrcode.model.SensorMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import okio.ByteString.Companion.decodeHex
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors
import javax.inject.Inject

internal class QrCodeAnalyserUseCase @Inject constructor(private val context: Context) {

    private val scanner = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
        .let { BarcodeScanning.getClient(it) }
    private val executor = Executors.newCachedThreadPool()

    @Suppress("MagicNumber")
    @OptIn(FlowPreview::class)
    fun analyse(controller: CameraController) = callbackFlow<List<Barcode>> {
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
        .mapNotNull { regex.find(it)?.groupValues?.subList(1, 5) }
        .map { it.map { stringHex -> stringHex.decodeHex().toByteArray() } }
        .map { idsBytes ->
            idsBytes.map { idBytes ->
                ByteBuffer
                    .wrap(byteArrayOf(0x00) + idBytes)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .int
            }
        }
        .map { intIds ->
            SensorMap(
                Sensor(intIds[0], SensorLocation.FRONT_LEFT),
                Sensor(intIds[1], SensorLocation.FRONT_RIGHT),
                Sensor(intIds[2], SensorLocation.REAR_LEFT),
                Sensor(intIds[3], SensorLocation.REAR_RIGHT)
            )
        }

    fun missingPermission() = CAMERA
        .takeIf { checkSelfPermission(context, CAMERA) != PERMISSION_GRANTED }

    companion object {
        // Test available here: https://regex101.com/r/c2xslp/1
        private val regex =
            "([0-9a-fA-F]{6})[&]([0-9a-fA-F]{6})[&]([0-9a-fA-F]{6})[&]([0-9a-fA-F]{6})".toRegex()
    }
}
