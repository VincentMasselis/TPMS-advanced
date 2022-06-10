package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.masselis.tpmsadvanced.R

@Composable
fun Car(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier) {
        val (car, frl, frr, rel, rer) = createRefs()
        Image(
            ImageBitmap.imageResource(id = R.drawable.schema_car_top_view),
            null,
            Modifier
                .height(462.dp)
                .width(208.dp)
                .constrainAs(car) { centerTo(parent) }
        )
        Tire(
            Modifier.constrainAs(frl) {
                start.linkTo(car.start, 12.dp)
                top.linkTo(car.top, 70.dp)
            }
        )
        Tire(
            Modifier.constrainAs(frr) {
                end.linkTo(car.end, 12.dp)
                top.linkTo(car.top, 70.dp)
            }
        )
        Tire(
            Modifier.constrainAs(rel) {
                start.linkTo(car.start, 10.dp)
                bottom.linkTo(car.bottom, 70.dp)
            }
        )
        Tire(
            Modifier.constrainAs(rer) {
                end.linkTo(car.end, 10.dp)
                bottom.linkTo(car.bottom, 70.dp)
            }
        )
    }
}

@Preview
@Composable
fun CarPreview() {
    Car()
}