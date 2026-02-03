package com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.screen

import android.animation.ArgbEvaluator
import androidx.car.app.Screen
import androidx.car.app.model.CarColor.createCustom
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.IconCompat.createWithResource
import com.masselis.tpmsadvanced.core.ui.DarkColors
import com.masselis.tpmsadvanced.core.ui.LightColors
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.androidauto.R
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.appendLoc
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow.State.Alerting
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow.State.DetectionIssue
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow.State.Normal
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow.State.NotDetected
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow

context(screen: Screen)
@Suppress("FunctionName", "FunctionNaming")
internal fun TyreGridItem(
    location: Vehicle.Kind.Location,
    iconState: TyreIconStateFlow.State,
    statsState: TyreStatsStateFlow.State
) = GridItem
    .Builder()
    .setTitle(buildString { appendLoc(location, false, capitalized = true) })
    .setImage(
        when (iconState) {
            Alerting -> tyreAlertingIcon()
            is Normal -> tyreNormalIcon(iconState)
            NotDetected -> tyreNotDetectedIcon()
            DetectionIssue -> tyreDetectionIssueIcon()
        }
    )
    .setText(
        when (statsState) {
            is TyreStatsStateFlow.State.Normal -> buildString {
                append(statsState.pressure.string(statsState.pressureUnit, true))
                append("  ")
                append(statsState.temperature.string(statsState.temperatureUnit, true))
            }

            is TyreStatsStateFlow.State.Alerting -> buildString {
                append(statsState.pressure.string(statsState.pressureUnit, true))
                append("  ")
                append(statsState.temperature.string(statsState.temperatureUnit, true))
            }

            TyreStatsStateFlow.State.NotDetected -> "-.-"
        }
    )
    .build()

context(screen: Screen)
private fun tyreNotDetectedIcon() = CarIcon
    .Builder(createWithResource(screen.carContext, R.drawable.car_tire_alert_outline))
    .setTint(createCustom(LightColors.tertiary.toArgb(), DarkColors.tertiary.toArgb()))
    .build()

private val evaluator = ArgbEvaluator()

context(screen: Screen)
private fun tyreNormalIcon(state: Normal) = CarIcon
    .Builder(createWithResource(screen.carContext, R.drawable.car_tire_no_alert))
    .setTint(
        createCustom(
            evaluator.evaluate(
                state.fraction.value,
                when (state) {
                    is Normal.BlueToGreen -> LightColors.tertiary.toArgb()
                    is Normal.GreenToRed -> LightColors.primary.toArgb()
                },
                when (state) {
                    is Normal.BlueToGreen -> LightColors.primary.toArgb()
                    is Normal.GreenToRed -> LightColors.error.toArgb()
                },
            ) as Int,
            evaluator.evaluate(
                state.fraction.value,
                when (state) {
                    is Normal.BlueToGreen -> DarkColors.tertiary.toArgb()
                    is Normal.GreenToRed -> DarkColors.primary.toArgb()
                },
                when (state) {
                    is Normal.BlueToGreen -> DarkColors.primary.toArgb()
                    is Normal.GreenToRed -> DarkColors.error.toArgb()
                },
            ) as Int,
        )
    )
    .build()

context(screen: Screen)
private fun tyreAlertingIcon() = CarIcon
    .Builder(createWithResource(screen.carContext, R.drawable.car_tire_alert))
    .setTint(createCustom(LightColors.error.toArgb(), DarkColors.error.toArgb()))
    .build()

context(screen: Screen)
private fun tyreDetectionIssueIcon() = CarIcon
    .Builder(createWithResource(screen.carContext, R.drawable.car_tire_alert_outline))
    .setTint(createCustom(LightColors.error.toArgb(), DarkColors.error.toArgb()))
    .build()
