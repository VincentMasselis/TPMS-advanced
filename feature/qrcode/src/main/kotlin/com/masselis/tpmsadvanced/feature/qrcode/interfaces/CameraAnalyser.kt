package com.masselis.tpmsadvanced.feature.qrcode.interfaces

import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
internal class CameraAnalyser @Inject constructor() {

    private val scanner = BarcodeScannerOptions
        .Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
        .let { BarcodeScanning.getClient(it) }
    private val executor = Executors.newCachedThreadPool()

    fun findQrCode(controller: CameraController) =
        callbackFlow<List<Barcode>> {
            try {
                controller.setImageAnalysisAnalyzer(
                    executor,
                    MlKitAnalyzer(
                        listOf(scanner),
                        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                        executor
                    ) {
                        it.getValue(scanner)?.also { barcodes -> launch { send(barcodes) } }
                    }
                )
                // Fired by `CameraSelector.select(LinkedHashSet<CameraInternal> cameras)`
            } catch (exc: IllegalArgumentException) {
                throw CameraUnavailable(exc)
            }
            awaitClose { controller.clearImageAnalysisAnalyzer() }
        }.flowOn(Dispatchers.Main.immediate)
            .flatMapConcat { it.asFlow() }
            .filter { it.valueType == Barcode.TYPE_TEXT }
            .mapNotNull { it.rawValue }

    class CameraUnavailable(source: IllegalArgumentException) : IllegalArgumentException(source)
}
