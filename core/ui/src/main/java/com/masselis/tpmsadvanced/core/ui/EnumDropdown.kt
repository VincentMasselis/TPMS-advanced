package com.masselis.tpmsadvanced.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet

@Composable
public inline fun <reified T : Enum<T>> EnumDropdown(
    noinline label: @Composable () -> Unit,
    currentValue: T,
    noinline stringOf: (T) -> String,
    noinline onValue: (T) -> Unit,
    modifier: Modifier = Modifier,
): Unit = EnumDropdown(
    values = enumValues<T>().asIterable().toImmutableSet(),
    label = label,
    stringOf = stringOf,
    currentValue = currentValue,
    onValue = onValue,
    modifier = modifier,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun <T : Enum<T>> EnumDropdown(
    values: ImmutableSet<T>,
    label: @Composable () -> Unit,
    currentValue: T,
    stringOf: (T) -> String,
    onValue: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
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
                        onValue(value)
                        expanded = false
                    }
                )
            }
        }
    }
}
