package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Home() {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "My car") }) },
        content = {
            Car(
                {},
                {},
                {},
                {},
                Modifier
                    .fillMaxSize()
                    .padding(it)
            )
        }
    )
}

@Preview
@Composable
private fun HomePreview() {
    Home()
}