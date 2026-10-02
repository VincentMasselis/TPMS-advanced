package com.masselis.tpmsadvanced.feature.qrcode.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.CAR
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.DELTA_THREE_WHEELER
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Location.Wheel
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.SINGLE_AXLE_TRAILER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.TADPOLE_THREE_WHEELER
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.qrcode.interfaces.CameraAnalyser
import com.masselis.tpmsadvanced.feature.qrcode.model.QrCodeSensor
import com.masselis.tpmsadvanced.feature.qrcode.model.QrCodeSensors
import com.masselis.tpmsadvanced.feature.qrcode.usecase.tools.mockkCurrentVehicleUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
internal class QrCodeSensorUseCaseTest {

    private lateinit var cameraAnalyser: CameraAnalyser
    private lateinit var currentVehicleUseCase: CurrentVehicleUseCase

    @Before
    fun setup() {
        cameraAnalyser = mockk {
            every { findQrCode(any()) } returns MutableSharedFlow()
        }
        currentVehicleUseCase = mockkCurrentVehicleUseCase(mockk {
            every { vehicle.kind } returns CAR
        })
    }

    private fun test() = QrCodeSensorUseCase(cameraAnalyser, currentVehicleUseCase)

    @Test
    fun fourValidWheelsForACar() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&21563D&31A4F0&41A552")
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.FourWheel(
                    QrCodeSensor(-1951592192, FRONT_LEFT),
                    QrCodeSensor(1029054720, FRONT_RIGHT),
                    QrCodeSensor(-257675008, REAR_LEFT),
                    QrCodeSensor(1386561792, REAR_RIGHT),
                ),
                qrCodeSensor
            )
            assertContentEquals(emptyList(), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onlyTwoWheelsForACar() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&21563D")
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.TwoWheel(
                    QrCodeSensor(-1951592192, FRONT_LEFT),
                    QrCodeSensor(1029054720, FRONT_RIGHT),
                ),
                qrCodeSensor
            )
            assertContentEquals(listOf(REAR_LEFT, REAR_RIGHT), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun twoWheelsForAMotorcycle() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&41A552")
        currentVehicleUseCase = mockkCurrentVehicleUseCase(mockk {
            every { vehicle.kind } returns MOTORCYCLE
        })
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.TwoWheel(
                    QrCodeSensor(-1951592192, FRONT_LEFT),
                    QrCodeSensor(1386561792, REAR_RIGHT),
                ),
                qrCodeSensor
            )
            assertContentEquals(emptyList(), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun twoBadLocatedWheelsForATrailer() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&31A4F0")
        currentVehicleUseCase = mockkCurrentVehicleUseCase(mockk {
            every { vehicle.kind } returns SINGLE_AXLE_TRAILER
        })
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.TwoWheel(
                    QrCodeSensor(-1951592192, FRONT_LEFT),
                    QrCodeSensor(-257675008, REAR_LEFT),
                ),
                qrCodeSensor
            )
            assertContentEquals(listOf(Location.Side.RIGHT), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun fourValidWheelsForATadpole() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&21563D&31A4F0&41A552")
        currentVehicleUseCase = mockkCurrentVehicleUseCase(mockk {
            every { vehicle.kind } returns TADPOLE_THREE_WHEELER
        })
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.FourWheel(
                    QrCodeSensor(-1951592192, FRONT_LEFT),
                    QrCodeSensor(1029054720, FRONT_RIGHT),
                    QrCodeSensor(-257675008, REAR_LEFT),
                    QrCodeSensor(1386561792, REAR_RIGHT),
                ),
                qrCodeSensor
            )
            assertContentEquals(emptyList(), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun fourValidWheelsForADelta() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&21563D&31A4F0&41A552")
        currentVehicleUseCase = mockkCurrentVehicleUseCase(mockk {
            every { vehicle.kind } returns DELTA_THREE_WHEELER
        })
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.FourWheel(
                    QrCodeSensor(-1951592192, FRONT_LEFT),
                    QrCodeSensor(1029054720, FRONT_RIGHT),
                    QrCodeSensor(-257675008, REAR_LEFT),
                    QrCodeSensor(1386561792, REAR_RIGHT),
                ),
                qrCodeSensor
            )
            assertContentEquals(emptyList(), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
