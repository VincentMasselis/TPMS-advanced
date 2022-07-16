package com.masselis.tpmsadvanced.uicommon

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> EnumDropdown(
    label: @Composable () -> Unit,
    stringOf: (T) -> String,
    values: Array<T>,
    modifier: Modifier = Modifier,
    mutableStateFlow: MutableStateFlow<T>
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