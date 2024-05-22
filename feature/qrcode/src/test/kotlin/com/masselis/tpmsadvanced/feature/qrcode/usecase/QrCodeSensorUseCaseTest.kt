package com.masselis.tpmsadvanced.feature.qrcode.usecase

import app.cash.turbine.test
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.CAR
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.DELTA_THREE_WHEELER
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Wheel
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
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

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
    fun `four valid wheels for a car`() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&21563D&31A4F0&41A552")
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.FourWheel(
                    QrCodeSensor(-1951592192, Wheel(FRONT_LEFT)),
                    QrCodeSensor(1029054720, Wheel(FRONT_RIGHT)),
                    QrCodeSensor(-257675008, Wheel(REAR_LEFT)),
                    QrCodeSensor(1386561792, Wheel(REAR_RIGHT)),
                ),
                qrCodeSensor
            )
            assertContentEquals(emptyList(), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `only two wheels for a car`() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&21563D")
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.TwoWheel(
                    QrCodeSensor(-1951592192, Wheel(FRONT_LEFT)),
                    QrCodeSensor(1029054720, Wheel(FRONT_RIGHT)),
                ),
                qrCodeSensor
            )
            assertContentEquals(listOf(Wheel(REAR_LEFT), Wheel(REAR_RIGHT)), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `two wheels for a motorcycle`() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&41A552")
        currentVehicleUseCase = mockkCurrentVehicleUseCase(mockk {
            every { vehicle.kind } returns MOTORCYCLE
        })
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.TwoWheel(
                    QrCodeSensor(-1951592192, Wheel(FRONT_LEFT)),
                    QrCodeSensor(1386561792, Wheel(REAR_RIGHT)),
                ),
                qrCodeSensor
            )
            assertContentEquals(emptyList(), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `two bad located wheels for a trailer`() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&31A4F0")
        currentVehicleUseCase = mockkCurrentVehicleUseCase(mockk {
            every { vehicle.kind } returns SINGLE_AXLE_TRAILER
        })
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.TwoWheel(
                    QrCodeSensor(-1951592192, Wheel(FRONT_LEFT)),
                    QrCodeSensor(-257675008, Wheel(REAR_LEFT)),
                ),
                qrCodeSensor
            )
            assertContentEquals(listOf(Location.Side(RIGHT)), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `four valid wheels for a tadpole`() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&21563D&31A4F0&41A552")
        currentVehicleUseCase = mockkCurrentVehicleUseCase(mockk {
            every { vehicle.kind } returns TADPOLE_THREE_WHEELER
        })
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.FourWheel(
                    QrCodeSensor(-1951592192, Wheel(FRONT_LEFT)),
                    QrCodeSensor(1029054720, Wheel(FRONT_RIGHT)),
                    QrCodeSensor(-257675008, Wheel(REAR_LEFT)),
                    QrCodeSensor(1386561792, Wheel(REAR_RIGHT)),
                ),
                qrCodeSensor
            )
            assertContentEquals(emptyList(), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `four valid wheels for a delta`() = runTest {
        every { cameraAnalyser.findQrCode(any()) } returns MutableStateFlow("11AD8B&21563D&31A4F0&41A552")
        currentVehicleUseCase = mockkCurrentVehicleUseCase(mockk {
            every { vehicle.kind } returns DELTA_THREE_WHEELER
        })
        test().analyse(mockk()).test {
            val (qrCodeSensor, missingLocations) = awaitItem()
            assertEquals(
                QrCodeSensors.FourWheel(
                    QrCodeSensor(-1951592192, Wheel(FRONT_LEFT)),
                    QrCodeSensor(1029054720, Wheel(FRONT_RIGHT)),
                    QrCodeSensor(-257675008, Wheel(REAR_LEFT)),
                    QrCodeSensor(1386561792, Wheel(REAR_RIGHT)),
                ),
                qrCodeSensor
            )
            assertContentEquals(emptyList(), missingLocations)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
