package com.masselis.tpmsadvanced

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.masselis.tpmsadvanced.AutomotiveComponent.Companion.HomeViewModelFactory
import com.masselis.tpmsadvanced.core.common.BuildConfig
import com.masselis.tpmsadvanced.core.ui.LocalHomeNavController
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.LocalVehicleComponent
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.Settings

internal class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsView()
        }
    }
}

@Composable
private fun SettingsView(
    viewModel: HomeViewModel = viewModel(key = "HomeViewModel") {
        HomeViewModelFactory(null)
    }
): Unit {
    val vehicleComponent by viewModel.vehicleComponentStateFlow.collectAsState()
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalVehicleComponent provides vehicleComponent,
        LocalHomeNavController provides navController
    ) {
        TpmsAdvancedTheme {
            Scaffold(
                content = { paddingValues ->
                    val modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                    Settings(modifier = modifier)
                }
            )
        }
    }

}