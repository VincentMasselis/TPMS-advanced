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
                startDestination = Paths.Home.path
            ) {
                composable(Paths.Home.path) {
                    Car(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    )
                }
                composable(Paths.Settings.path) {
                    Settings(
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
    val currentPath = currentEntry?.destination?.route?.let { Paths.from(it) }
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = when (currentPath) {
                    Paths.Home -> "My car"
                    Paths.Settings -> "Settings"
                    else -> ""
                }
            )
        },
        navigationIcon = {
            when (currentPath) {
                Paths.Settings -> {
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
                Paths.Home, null -> {}
            }
        },
        actions = {
            when (currentPath) {
                Paths.Home ->
                    IconButton(onClick = {
                        navController.navigate(Paths.Settings.path)
                    }) {
                        Icon(
                            bitmap = ImageBitmap.imageResource(R.drawable.ic_car_cog_black_24dp),
                            contentDescription = null,
                        )
                    }
                Paths.Settings, null -> {}
            }
        }
    )
}

sealed class Paths(val path: String) {
    object Home : Paths("home")
    object Settings : Paths("home/settings")

    companion object {
        fun from(string: String) = when (string) {
            "home" -> Home
            "home/settings" -> Settings
            else -> throw IllegalArgumentException()
        }
    }
}