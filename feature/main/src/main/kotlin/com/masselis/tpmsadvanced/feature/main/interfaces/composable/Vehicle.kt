@file:Suppress("LongMethod")

package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.masselis.tpmsadvanced.core.ui.KeepScreenOn
import com.masselis.tpmsadvanced.data.vehicle.model.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind
import com.masselis.tpmsadvanced.feature.main.R
import com.masselis.tpmsadvanced.feature.main.ioc.vehicle.VehicleComponent

@Composable
public fun CurrentVehicle(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Vehicle(
        component = LocalVehicleComponent.current,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
public fun Vehicle(
    component: VehicleComponent,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    KeepScreenOn()
    when (component.vehicle.kind) {
        Kind.CAR -> Car(snackbarHostState, modifier)
        Kind.SINGLE_AXLE_TRAILER -> SingleAxleTrailer(snackbarHostState, modifier)
        Kind.MOTORCYCLE -> Motorcycle(snackbarHostState, modifier)
        Kind.TADPOLE_THREE_WHEELER -> TadpoleThreadWheeler(snackbarHostState, modifier)
        Kind.DELTA_THREE_WHEELER -> DeltaThreeWheeler(snackbarHostState, modifier)
    }
}

@Composable
private fun Car(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (
            carConst,
            tyreBox,
            frontLeft,
            frontLeftStats,
            frontLeftIconContainer,
            frontRight,
            frontRightStats,
            frontRightIconContainer,
            rearLeft,
            rearLeftStats,
            rearLeftIconContainer,
            rearRight,
            rearRightStats,
            rearRightIconContainer,
        ) = createRefs()
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.schema_car_top_view),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentDescription = "Image of your car",
            modifier = Modifier
                .aspectRatio(208f / 462f)
                .constrainAs(carConst) {
                    centerTo(parent)
                    height = Dimension.percent(.7f)
                }
        )
        Box(
            Modifier
                .aspectRatio(235f / 462f)
                .constrainAs(tyreBox) {
                    centerTo(parent)
                    height = Dimension.percent(.55f)
                }
        )
        with(Location.Wheel.FRONT_LEFT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.constrainAs(frontLeft) {
                    top.linkTo(tyreBox.top)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontLeftStats) {
                    top.linkTo(frontLeft.top)
                    end.linkTo(frontLeft.start, 8.dp)
                    // If not, the word "bar" for "1,50 bar" is not displayed 🤷
                    width = Dimension.value(100.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(frontLeftIconContainer) {
                    top.linkTo(frontLeft.top)
                    bottom.linkTo(frontLeft.bottom)
                    start.linkTo(frontLeft.end)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
        with(Location.Wheel.FRONT_RIGHT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.constrainAs(frontRight) {
                    top.linkTo(tyreBox.top)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontRightStats) {
                    top.linkTo(frontRight.top)
                    start.linkTo(frontRight.end, 8.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(frontRightIconContainer) {
                    top.linkTo(frontRight.top)
                    bottom.linkTo(frontRight.bottom)
                    end.linkTo(frontRight.start)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
        with(Location.Wheel.REAR_LEFT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.constrainAs(rearLeft) {
                    bottom.linkTo(tyreBox.bottom)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearLeftStats) {
                    bottom.linkTo(rearLeft.bottom)
                    end.linkTo(rearLeft.start, 8.dp)
                    width = Dimension.value(100.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(rearLeftIconContainer) {
                    top.linkTo(rearLeft.top)
                    bottom.linkTo(rearLeft.bottom)
                    start.linkTo(rearLeft.end)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
        with(Location.Wheel.REAR_RIGHT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.constrainAs(rearRight) {
                    bottom.linkTo(tyreBox.bottom)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearRightStats) {
                    bottom.linkTo(rearRight.bottom)
                    start.linkTo(rearRight.end, 8.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(rearRightIconContainer) {
                    top.linkTo(rearRight.top)
                    bottom.linkTo(rearRight.bottom)
                    end.linkTo(rearRight.start)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
    }
}

@Composable
private fun SingleAxleTrailer(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (
            askHelp,
            tyreBox,
            tyreLeft,
            leftStats,
            leftIconContainer,
            tyreRight,
            rightStats,
            rightIconContainer,
        ) = createRefs()
        BackgroundImageAskHelp(
            Modifier.constrainAs(askHelp) {
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom, 4.dp)
            }
        )
        Box(
            Modifier
                .aspectRatio(235f / 462f)
                .constrainAs(tyreBox) {
                    centerTo(parent)
                    height = Dimension.percent(.55f)
                }
        )
        with(Location.Side.LEFT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .constrainAs(tyreLeft) {
                        start.linkTo(tyreBox.start)
                        top.linkTo(tyreBox.top)
                        bottom.linkTo(tyreBox.bottom)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(leftStats) {
                    top.linkTo(tyreLeft.top)
                    bottom.linkTo(tyreLeft.bottom)
                    end.linkTo(tyreLeft.start, 8.dp)
                    width = Dimension.value(100.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(leftIconContainer) {
                    top.linkTo(tyreLeft.top)
                    bottom.linkTo(tyreLeft.bottom)
                    start.linkTo(tyreLeft.end)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
        with(Location.Side.RIGHT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .constrainAs(tyreRight) {
                        end.linkTo(tyreBox.end)
                        top.linkTo(tyreBox.top)
                        bottom.linkTo(tyreBox.bottom)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rightStats) {
                    top.linkTo(tyreRight.top)
                    bottom.linkTo(tyreRight.bottom)
                    start.linkTo(tyreRight.end, 8.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(rightIconContainer) {
                    top.linkTo(tyreRight.top)
                    bottom.linkTo(tyreRight.bottom)
                    end.linkTo(tyreRight.start)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
    }
}

@Composable
private fun Motorcycle(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (
            vehicleImage,
            tyreBox,
            tyreFront,
            frontStats,
            frontBinding,
            frontIconInfo,
            tyreRear,
            rearStats,
            rearBinding,
            rearIconInfo,
        ) = createRefs()
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.schema_motorcycle_top_view),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentDescription = "Image of your motorcycle",
            modifier = Modifier
                .aspectRatio(208f / 462f)
                .constrainAs(vehicleImage) {
                    centerTo(parent)
                    height = Dimension.percent(.7f)
                }
        )
        Box(
            Modifier
                .aspectRatio(235f / 462f)
                .constrainAs(tyreBox) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent, 0.4f)
                    height = Dimension.percent(.65f)
                }
        )
        with(Location.Axle.FRONT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .constrainAs(tyreFront) {
                        start.linkTo(tyreBox.start)
                        end.linkTo(tyreBox.end)
                        top.linkTo(tyreBox.top)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontStats) {
                    centerHorizontallyTo(tyreFront)
                    bottom.linkTo(vehicleImage.top, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier.constrainAs(frontBinding) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    end.linkTo(tyreFront.start)
                }
            )
            TyreInfoButton(
                location = this,
                modifier = Modifier.constrainAs(frontIconInfo) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    start.linkTo(tyreFront.end)
                }
            )
        }
        with(Location.Axle.REAR) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .constrainAs(tyreRear) {
                        start.linkTo(tyreBox.start)
                        end.linkTo(tyreBox.end)
                        bottom.linkTo(tyreBox.bottom)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearStats) {
                    centerHorizontallyTo(tyreRear)
                    top.linkTo(vehicleImage.bottom, 8.dp)
                }
            )
            BindSensorButton(
                location = this,
                modifier = Modifier
                    .constrainAs(rearBinding) {
                        top.linkTo(tyreRear.top)
                        bottom.linkTo(tyreRear.bottom)
                        end.linkTo(tyreRear.start)
                    }
            )
            TyreInfoButton(
                location = this,
                modifier = Modifier.constrainAs(rearIconInfo) {
                    top.linkTo(tyreRear.top)
                    bottom.linkTo(tyreRear.bottom)
                    start.linkTo(tyreRear.end)
                }
            )
        }
    }
}

@Composable
private fun TadpoleThreadWheeler(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (
            askHelp,
            tyreBox,
            frontLeft,
            frontLeftStats,
            frontLeftIcons,
            frontRight,
            frontRightStats,
            frontRightIcons,
            tyreRear,
            rearStats,
            rearIcons,
        ) = createRefs()
        BackgroundImageAskHelp(
            Modifier.constrainAs(askHelp) {
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom, 4.dp)
            }
        )
        Box(
            Modifier
                .aspectRatio(235f / 462f)
                .constrainAs(tyreBox) {
                    centerTo(parent)
                    height = Dimension.percent(.55f)
                }
        )
        with(Location.Wheel.FRONT_LEFT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.constrainAs(frontLeft) {
                    top.linkTo(tyreBox.top)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontLeftStats) {
                    top.linkTo(frontLeft.top)
                    end.linkTo(frontLeft.start, 8.dp)
                    // If not, the word "bar" for "1,50 bar" is not displayed 🤷
                    width = Dimension.value(100.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(frontLeftIcons) {
                    top.linkTo(frontLeft.top)
                    bottom.linkTo(frontLeft.bottom)
                    start.linkTo(frontLeft.end)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
        with(Location.Wheel.FRONT_RIGHT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.constrainAs(frontRight) {
                    top.linkTo(tyreBox.top)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontRightStats) {
                    top.linkTo(frontRight.top)
                    start.linkTo(frontRight.end, 8.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(frontRightIcons) {
                    top.linkTo(frontRight.top)
                    bottom.linkTo(frontRight.bottom)
                    end.linkTo(frontRight.start)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
        with(Location.Axle.REAR) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .constrainAs(tyreRear) {
                        start.linkTo(tyreBox.start)
                        end.linkTo(tyreBox.end)
                        bottom.linkTo(tyreBox.bottom)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearStats) {
                    top.linkTo(tyreRear.top)
                    bottom.linkTo(tyreRear.bottom)
                    start.linkTo(tyreRear.end, 8.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(rearIcons) {
                    top.linkTo(tyreRear.top)
                    bottom.linkTo(tyreRear.bottom)
                    end.linkTo(tyreRear.start)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
    }
}

@Composable
private fun DeltaThreeWheeler(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (
            askHelp,
            tyreBox,
            tyreFront,
            frontStats,
            frontIcons,
            rearLeft,
            rearLeftStats,
            rearLeftIcons,
            rearRight,
            rearRightStats,
            rearRightIcons
        ) = createRefs()
        BackgroundImageAskHelp(
            Modifier.constrainAs(askHelp) {
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom, 4.dp)
            }
        )
        Box(
            Modifier
                .aspectRatio(235f / 462f)
                .constrainAs(tyreBox) {
                    centerTo(parent)
                    height = Dimension.percent(.55f)
                }
        )
        with(Location.Axle.FRONT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .constrainAs(tyreFront) {
                        start.linkTo(tyreBox.start)
                        end.linkTo(tyreBox.end)
                        top.linkTo(tyreBox.top)
                        width = Dimension.value(30.dp)
                        height = Dimension.value(100.dp)
                    }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(frontStats) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    start.linkTo(tyreFront.end, 8.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(frontIcons) {
                    top.linkTo(tyreFront.top)
                    bottom.linkTo(tyreFront.bottom)
                    end.linkTo(tyreFront.start)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
        with(Location.Wheel.REAR_LEFT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.constrainAs(rearLeft) {
                    bottom.linkTo(tyreBox.bottom)
                    start.linkTo(tyreBox.start)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearLeftStats) {
                    bottom.linkTo(rearLeft.bottom)
                    end.linkTo(rearLeft.start, 8.dp)
                    width = Dimension.value(100.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(rearLeftIcons) {
                    top.linkTo(rearLeft.top)
                    bottom.linkTo(rearLeft.bottom)
                    start.linkTo(rearLeft.end)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
        with(Location.Wheel.REAR_RIGHT) {
            Tyre(
                location = this,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.constrainAs(rearRight) {
                    bottom.linkTo(tyreBox.bottom)
                    end.linkTo(tyreBox.end)
                    width = Dimension.value(30.dp)
                    height = Dimension.value(100.dp)
                }
            )
            TyreStat(
                location = this,
                modifier = Modifier.constrainAs(rearRightStats) {
                    bottom.linkTo(rearRight.bottom)
                    start.linkTo(rearRight.end, 8.dp)
                }
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(rearRightIcons) {
                    top.linkTo(rearRight.top)
                    bottom.linkTo(rearRight.bottom)
                    end.linkTo(rearRight.start)
                }
            ) {
                BindSensorButton(location = this@with)
                TyreInfoButton(location = this@with)
            }
        }
    }
}

@Composable
private fun BackgroundImageAskHelp(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "This app is built by its community,\nThis screen needs a background image",
            fontWeight = FontWeight.Light,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.End,
        )
        Spacer(Modifier.width(8.dp))
        OutlinedButton(
            onClick = {
                try {
                    context.startActivity(
                        Intent(
                            ACTION_VIEW,
                            Uri.parse(
                                "https://github.com/" +
                                        "VincentMasselis/" +
                                        "TPMS-advanced/" +
                                        "issues/" +
                                        "new?" +
                                        "labels=enhancement&template=vehicle-background-image-proposal.md"
                            )
                        )
                    )
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(context, "No web browser found", Toast.LENGTH_LONG).show()
                }
            },
            contentPadding = PaddingValues(
                top = 4.dp,
                bottom = 4.dp,
                start = 12.dp,
                end = 12.dp
            ),
            content = {
                Text(
                    text = "Help us",
                    fontSize = 12.sp,
                )
            }
        )
    }
}
