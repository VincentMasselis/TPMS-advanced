package com.masselis.tpmsadvanced.qrcode.usecase

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.masselis.tpmsadvanced.common.appContext
import com.masselis.tpmsadvanced.core.usecase.SensorIdUseCase
import com.masselis.tpmsadvanced.qrcode.model.SensorIds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okio.ByteString.Companion.decodeHex
import java.util.concurrent.Executors
import javax.inject.Inject

class QrCodeAnalyserUseCase @Inject constructor(
    private val sensorIdUseCase: SensorIdUseCase,
) {

    private val scanner = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
        .let { BarcodeScanning.getClient(it) }
    private val executor = Executors.newCachedThreadPool()

    @OptIn(FlowPreview::class)
    fun analyse(controller: CameraController) = callbackFlow<List<Barcode>> {
        controller.setImageAnalysisAnalyzer(
            executor,
            MlKitAnalyzer(
                listOf(scanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                executor
            ) {
                launch { send(it.getValue(scanner)!!) }
            }
        )
        awaitClose { controller.clearImageAnalysisAnalyzer() }
    }.flowOn(Dispatchers.Main.immediate)
        .flatMapConcat { it.asFlow() }
        .filter { it.valueType == Barcode.TYPE_TEXT }
        .mapNotNull { it.rawValue }
        .mapNotNull { regex.find(it)?.groupValues?.subList(1, 5) }
        .map { stringIds -> stringIds.map { sensorIdUseCase.asInt(it.decodeHex().toByteArray()) } }
        .map { intIds -> SensorIds(intIds[0], intIds[1], intIds[2], intIds[3]) }

    fun missingPermission() = CAMERA
        .takeIf { checkSelfPermission(appContext, CAMERA) != PERMISSION_GRANTED }

    companion object {
        // Test available here: https://regex101.com/r/c2xslp/1
        private val regex =
            "([0-9a-fA-F]{6})[&]([0-9a-fA-F]{6})[&]([0-9a-fA-F]{6})[&]([0-9a-fA-F]{6})".toRegex()
    }
}