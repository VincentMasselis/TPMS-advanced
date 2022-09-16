package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
internal fun Test() {
    Box(
        Modifier
            .clip(RoundedCornerShape(percent = 20))
            .aspectRatio(15f / 40f)
            .background(Color.Transparent)
            .border(2.dp, Color.Black, RoundedCornerShape(percent = 20))
    )
}