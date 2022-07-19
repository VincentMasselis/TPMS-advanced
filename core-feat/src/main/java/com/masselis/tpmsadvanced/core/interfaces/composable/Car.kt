package com.masselis.tpmsadvanced.core.interfaces.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.masselis.tpmsadvanced.core.model.TyreLocation
import com.masselis.tpmsadvanced.uicommon.KeepScreenOn

@Composable
public fun Car(modifier: Modifier = Modifier) {
    KeepScreenOn()
    ConstraintLayout(modifier = modifier) {
        val (car, tyreBox, topLeftStats, topLeftFav, topRightStats, topRightFav, bottomLeftStats, bottomLeftFav, bottomRightStats, bottomRightFav) = createRefs()
        Image(
            bitmap = ImageBitmap.imageResource(id = com.masselis.tpmsadvanced.core.R.drawable.schema_car_top_view),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(208f / 462f)
                .constrainAs(car) {
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
        ) {
            Tyre(
                location = TyreLocation.FRONT_LEFT,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxHeight(.2f)
                    .fillMaxWidth(.1f)
            )
            Tyre(
                location = TyreLocation.FRONT_RIGHT,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight(.2f)
                    .fillMaxWidth(.1f)
            )
            Tyre(
                location = TyreLocation.REAR_LEFT,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxHeight(.2f)
                    .fillMaxWidth(.12f)
            )
            Tyre(
                location = TyreLocation.REAR_RIGHT,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxHeight(.2f)
                    .fillMaxWidth(.12f)
            )
        }
        Box(Modifier
            .constrainAs(topLeftStats) {
                top.linkTo(tyreBox.top)
                end.linkTo(tyreBox.start, 8.dp)
            }
            .width(100.dp)
        ) {
            TyreStat(
                location = TyreLocation.FRONT_LEFT,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        Box(Modifier.constrainAs(topLeftFav) {
            top.linkTo(tyreBox.top)
            start.linkTo(tyreBox.start)
        }) {
            FavouriteButton(
                TyreLocation.FRONT_LEFT,
                modifier = Modifier.padding(start = 24.dp)
            )
        }
        Box(Modifier
            .constrainAs(topRightStats) {
                top.linkTo(tyreBox.top)
                start.linkTo(tyreBox.end, 8.dp)
            }
            .width(100.dp)
        ) {
            TyreStat(
                location = TyreLocation.FRONT_RIGHT,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        Box(Modifier.constrainAs(topRightFav) {
            top.linkTo(tyreBox.top)
            end.linkTo(tyreBox.end)
        }) {
            FavouriteButton(
                TyreLocation.FRONT_RIGHT,
                modifier = Modifier.padding(end = 24.dp)
            )
        }
        Box(Modifier
            .constrainAs(bottomLeftStats) {
                bottom.linkTo(tyreBox.bottom)
                end.linkTo(tyreBox.start, 8.dp)
            }
            .width(100.dp)
        ) {
            TyreStat(
                location = TyreLocation.REAR_LEFT,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        Box(Modifier.constrainAs(bottomLeftFav) {
            bottom.linkTo(tyreBox.bottom)
            start.linkTo(tyreBox.start)
        }) {
            FavouriteButton(
                TyreLocation.REAR_LEFT,
                modifier = Modifier.padding(start = 28.dp)
            )
        }
        Box(Modifier
            .constrainAs(bottomRightStats) {
                bottom.linkTo(tyreBox.bottom)
                start.linkTo(tyreBox.end, 8.dp)
            }
            .width(100.dp)
        ) {
            TyreStat(
                location = TyreLocation.REAR_RIGHT,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        Box(Modifier.constrainAs(bottomRightFav) {
            bottom.linkTo(tyreBox.bottom)
            end.linkTo(tyreBox.end)
        }) {
            FavouriteButton(
                TyreLocation.REAR_RIGHT,
                modifier = Modifier.padding(end = 28.dp)
            )
        }
    }
}