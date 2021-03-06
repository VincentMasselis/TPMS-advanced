package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.masselis.tpmsadvanced.R

val HomeNavController = compositionLocalOf<NavController> {
    error("Nav controller not available")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    CompositionLocalProvider(HomeNavController provides rememberNavController()) {
        Scaffold(
            topBar = { TopAppBar() },
            content = { paddingValues ->
                NavHost(
                    navController = HomeNavController.current as NavHostController,
                    startDestination = Paths.Home.path
                ) {
                    val modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                    composable(Paths.Home.path) {
                        Car(modifier = modifier)
                    }
                    composable(Paths.QrCode.path) {
                        QrCodeScan(modifier = modifier)
                    }
                    composable(Paths.Settings.path) {
                        Settings(modifier = modifier)
                    }
                }
            }
        )
    }
}

@Composable
private fun TopAppBar() {
    val navController = HomeNavController.current
    val currentPath = navController.currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route
        ?.let { Paths.from(it) }
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
                Paths.Settings, Paths.QrCode -> {
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
                Paths.Home -> {
                    IconButton(onClick = {
                        navController.navigate(Paths.QrCode.path)
                    }) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.qrcode),
                            contentDescription = null,
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(Paths.Settings.path)
                    }) {
                        Icon(
                            ImageBitmap.imageResource(R.drawable.ic_car_cog_black_24dp),
                            contentDescription = null,
                        )
                    }
                }
                Paths.Settings, Paths.QrCode, null -> {}
            }
        }
    )
}

sealed class Paths(val path: String) {
    object Home : Paths("home")
    object QrCode : Paths("home/qrcode")
    object Settings : Paths("home/settings")

    companion object {
        fun from(string: String) = when (string) {
            "home" -> Home
            "home/qrcode" -> QrCode
            "home/settings" -> Settings
            else -> throw IllegalArgumentException()
        }
    }
}