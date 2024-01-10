package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masselis.tpmsadvanced.R
import com.masselis.tpmsadvanced.interfaces.composable.BindingMethod.MANUALLY
import com.masselis.tpmsadvanced.interfaces.composable.BindingMethod.QR_CODE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChooseBindingMethod(
    scanQrCode: () -> Unit,
    searchUnlocatedSensors: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bindingMethod by rememberSaveable { mutableStateOf(null as BindingMethod?) }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val qrCodeColor by animateColorAsState(
                targetValue =
                if (bindingMethod == QR_CODE) colorScheme.primary
                else colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 100),
                label = "qrCodeColor"
            )
            OutlinedCard(
                shape = RoundedCornerShape(percent = 20),
                border = BorderStroke(2.dp, qrCodeColor),
                onClick = { bindingMethod = QR_CODE },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Scan QR Code",
                    style = AppTypography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.sysgration_sensor),
                    contentDescription = "Sysgration sensor",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(100.dp)
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sysgration sensor",
                    style = AppTypography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                RadioButton(
                    selected = bindingMethod == QR_CODE,
                    onClick = { bindingMethod = QR_CODE },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            val manuallyColor by animateColorAsState(
                targetValue =
                if (bindingMethod == MANUALLY) colorScheme.primary
                else colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 100),
                label = "manuallyColor"
            )
            OutlinedCard(
                shape = RoundedCornerShape(percent = 20),
                border = BorderStroke(2.dp, manuallyColor),
                onClick = { bindingMethod = MANUALLY },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Bind manually",
                    style = AppTypography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.pecham),
                    contentDescription = "Pecham sensor",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(100.dp)
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pecham sensor",
                    style = AppTypography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                RadioButton(
                    selected = bindingMethod == MANUALLY,
                    onClick = { bindingMethod = MANUALLY },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        AnimatedVisibility(
            visible = bindingMethod != null,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    when (bindingMethod) {
                        QR_CODE -> scanQrCode()
                        MANUALLY -> searchUnlocatedSensors()
                        null -> error("")
                    }
                }) {
                    Text(
                        when (bindingMethod) {
                            QR_CODE -> "Scan QR Code"
                            MANUALLY -> "Bind sensor one by one"
                            null -> error("")
                        }
                    )
                }
            }
        }
    }
}

private enum class BindingMethod {
    QR_CODE,
    MANUALLY;
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun Preview() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp),
    ) {
        ChooseBindingMethod(
            scanQrCode = {},
            searchUnlocatedSensors = {}
        )
    }
}