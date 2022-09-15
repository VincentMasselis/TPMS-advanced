package com.masselis.tpmsadvanced.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
public inline fun <reified T : Enum<T>> EnumDropdown(
    noinline label: @Composable () -> Unit,
    noinline stringOf: (T) -> String,
    mutableStateFlow: MutableStateFlow<T>,
    modifier: Modifier = Modifier,
): Unit = EnumDropdown(
    label = label,
    stringOf = stringOf,
    values = enumValues(),
    mutableStateFlow = mutableStateFlow,
    modifier = modifier,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun <T : Enum<T>> EnumDropdown(
    label: @Composable () -> Unit,
    stringOf: (T) -> String,
    values: Array<T>,
    mutableStateFlow: MutableStateFlow<T>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val currentValue by mutableStateFlow.collectAsState()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = stringOf(currentValue),
            onValueChange = { },
            readOnly = true,
            label = label,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded }
        ) {
            values.forEach { value ->
                DropdownMenuItem(
                    text = { Text(stringOf(value)) },
                    onClick = {
                        mutableStateFlow.value = value
                        expanded = false
                    }
                )
            }
        }
    }
}
