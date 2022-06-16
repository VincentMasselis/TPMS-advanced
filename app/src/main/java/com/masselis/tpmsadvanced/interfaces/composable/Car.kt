package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.masselis.tpmsadvanced.R
import com.masselis.tpmsadvanced.model.TyreLocation

@Composable
fun Car(
    frontLeftData: @Composable () -> Unit,
    frontRightData: @Composable () -> Unit,
    bottomLeftData: @Composable () -> Unit,
    bottomRightData: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (car, tyreBox, topLeft, topRight, bottomLeft, bottomRight) = createRefs()
        Image(
            ImageBitmap.imageResource(id = R.drawable.schema_car_top_view),
            null,
            Modifier
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
        Box(Modifier.constrainAs(topLeft) {
            top.linkTo(tyreBox.top)
            end.linkTo(tyreBox.start, 8.dp)
        }) {
            frontLeftData()
        }
        Box(Modifier.constrainAs(topRight) {
            top.linkTo(tyreBox.top)
            start.linkTo(tyreBox.end, 8.dp)
        }) {
            frontRightData()
        }
        Box(Modifier.constrainAs(bottomLeft) {
            bottom.linkTo(tyreBox.bottom)
            end.linkTo(tyreBox.start, 8.dp)
        }) {
            bottomLeftData()
        }
        Box(Modifier.constrainAs(bottomRight) {
            bottom.linkTo(tyreBox.bottom)
            start.linkTo(tyreBox.end, 8.dp)
        }) {
            bottomRightData()
        }
    }
}

@Preview
@Composable
private fun CarPreview() {
    Car(
        { Text(text = "Top left") },
        { Text(text = "Top right") },
        { Text(text = "Bottom left") },
        { Text(text = "Bottom right") },
        Modifier
            .height(732.dp)
            .width(421.dp)
    )
}