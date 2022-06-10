package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Tire(modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(2.dp))
            .width(15.dp)
            .height(40.dp)
            .background(Color.Green)
    )
}