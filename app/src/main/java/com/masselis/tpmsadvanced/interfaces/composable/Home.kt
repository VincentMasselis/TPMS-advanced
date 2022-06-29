package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.masselis.tpmsadvanced.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar(navController) },
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Paths.HOME.name
            ) {
                composable(Paths.HOME.name) {
                    Car(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    )
                }
                composable(Paths.ALERT.name) {
                    Alert(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    )
                }
            }
        }
    )
}

@Composable
private fun TopAppBar(navController: NavHostController) {
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentPath = currentEntry?.destination?.route?.let { Paths.valueOf(it) }
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = when (currentPath) {
                    Paths.HOME -> "My car"
                    Paths.ALERT -> "Alerts"
                    else -> ""
                }
            )
        },
        navigationIcon = {
            when (currentPath) {
                Paths.ALERT -> {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        content = {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    )
                }
                Paths.HOME, null -> {}
            }
        },
        actions = {
            when (currentPath) {
                Paths.HOME ->
                    IconButton(onClick = {
                        navController.navigate(Paths.ALERT.name)
                    }) {
                        Icon(
                            bitmap = ImageBitmap.imageResource(R.drawable.ic_car_cog_black_24dp),
                            contentDescription = null,
                        )
                    }
                Paths.ALERT, null -> {}
            }
        }
    )
}

enum class Paths {
    HOME,
    ALERT;
}