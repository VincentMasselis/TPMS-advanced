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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masselis.tpmsadvanced.R
import com.masselis.tpmsadvanced.interfaces.composable.BindingMethod.MANUALLY
import com.masselis.tpmsadvanced.interfaces.composable.BindingMethod.QR_CODE

@Suppress("NAME_SHADOWING")
@Composable
internal fun ChooseBindingMethod(
    scanQrCode: () -> Unit,
    searchUnlocatedSensors: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bindingMethod by rememberSaveable { mutableStateOf(null as BindingMethod?) }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .testTag(ChooseBindingMethodTags.root)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Method(
                method = QR_CODE,
                isSelected = bindingMethod == QR_CODE,
                onClick = { bindingMethod = QR_CODE },
                modifier = Modifier
                    .weight(1f)
                    .testTag(ChooseBindingMethodTags.scanQrCodeRadioEntry)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Method(
                method = MANUALLY,
                isSelected = bindingMethod == MANUALLY,
                onClick = { bindingMethod = MANUALLY },
                modifier = Modifier
                    .weight(1f)
                    .testTag(ChooseBindingMethodTags.bindManuallyRadioEntry)
            )
        }
        AnimatedVisibility(
            visible = bindingMethod != null,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            val bindingMethod = bindingMethod!!
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        when (bindingMethod) {
                            QR_CODE -> scanQrCode()
                            MANUALLY -> searchUnlocatedSensors()
                        }
                    },
                    modifier = Modifier.testTag(ChooseBindingMethodTags.goNextButton)
                ) {
                    Text(
                        when (bindingMethod) {
                            QR_CODE -> "Scan QR Code"
                            MANUALLY -> "Bind sensor one by one"
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Method(
    method: BindingMethod,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue =
        if (isSelected) colorScheme.primary
        else colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 100),
        label = "color_animation_$method"
    )
    OutlinedCard(
        shape = RoundedCornerShape(percent = 20),
        border = BorderStroke(2.dp, borderColor),
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = when (method) {
                QR_CODE -> "Scan QR Code"
                MANUALLY -> "Bind manually"
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp, start = 4.dp, end = 4.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(
                id = when (method) {
                    QR_CODE -> R.drawable.sysgration_sensor
                    MANUALLY -> R.drawable.pecham_sensor
                }
            ),
            contentDescription = when (method) {
                QR_CODE -> "Sysgration sensors"
                MANUALLY -> "Pecham sensors"
            },
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(100.dp)
                .align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (method) {
                QR_CODE -> "Sysgration sensors"
                MANUALLY -> "Pecham sensors"
            },
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private enum class BindingMethod {
    QR_CODE,
    MANUALLY;
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFCCCCCC,
    device = Devices.NEXUS_5
)
@Composable
internal fun ChooseBindingMethodPreview() {
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

@Suppress("ConstPropertyName")
internal object ChooseBindingMethodTags {
    const val root = "ChooseBindingMethodTags_root"
    const val scanQrCodeRadioEntry = "ChooseBindingMethodTags_scanQrCodeRadioEntry"
    const val bindManuallyRadioEntry = "ChooseBindingMethodTags_bindManuallyRadioEntry"
    const val goNextButton = "ChooseBindingMethodTags_goNextButton"
}
