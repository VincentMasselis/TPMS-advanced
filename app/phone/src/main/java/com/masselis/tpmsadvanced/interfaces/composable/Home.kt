package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.masselis.tpmsadvanced.R
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdown
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.Vehicle
import com.masselis.tpmsadvanced.core.ui.LocalHomeNavController
import com.masselis.tpmsadvanced.core.ui.Spotlight
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags.backButton
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags.settings
import com.masselis.tpmsadvanced.interfaces.ioc.AppPhoneComponent
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel.SpotlightEvent
import com.masselis.tpmsadvanced.qrcode.interfaces.QrCodeScan
import kotlinx.coroutines.channels.consumeEach

@Suppress("LongMethod")
@Composable
internal fun Home(
    viewModel: HomeViewModel = viewModel {
        AppPhoneComponent.homeViewModel.build(createSavedStateHandle())
    }
) {
    CompositionLocalProvider(LocalHomeNavController provides rememberNavController()) {
        var offsetToFocus by remember { mutableStateOf<Offset?>(null) }
        var showCarListDropdownSpotlight by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    titleModifier = Modifier.onGloballyPositioned { coordinates ->
                        if (offsetToFocus != null) return@onGloballyPositioned
                        coordinates.positionInRoot()
                            .takeIf { it != Offset.Unspecified }
                            ?.let { topLeft ->
                                Offset(
                                    topLeft.x + coordinates.size.width.div(2),
                                    topLeft.y + coordinates.size.height.div(2)
                                )
                            }
                            ?.also { offsetToFocus = it }
                    },
                )
            },
            content = { paddingValues ->
                NavHost(
                    navController = LocalHomeNavController.current as NavHostController,
                    startDestination = Paths.Home.path
                ) {
                    val modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                    composable(Paths.Home.path) {
                        Vehicle(modifier = modifier)
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
        if (offsetToFocus != null)
            AnimatedVisibility(
                visible = showCarListDropdownSpotlight,
                enter = fadeIn(spring(0f)),
                exit = fadeOut()
            ) {
                with(LocalDensity.current) {
                    Spotlight(
                        center = offsetToFocus!!,
                        radius = 100.dp.toPx(),
                        text = AnnotatedString("Something new is waiting for you here,\nclick on \"Add a vehicle ⊕\""),
                        textPadding = 8.dp.toPx(),
                        textStyle = TextStyle.Default.copy(
                            color = Color.White,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        onSpotlight = { showCarListDropdownSpotlight = false }
                    )
                }
            }
        LaunchedEffect("EVENT") {
            viewModel.eventChannel.consumeEach {
                when (it) {
                    SpotlightEvent.CarListDropdown ->
                        showCarListDropdownSpotlight = true
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
private fun TopAppBar(
    modifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
) {
    val navController = LocalHomeNavController.current
    val currentPath = navController.currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route
        ?.let { Paths.from(it) }
    CenterAlignedTopAppBar(
        title = {
            when (currentPath) {
                Paths.Home -> CurrentVehicleDropdown(titleModifier)
                Paths.Settings -> Text(text = "Settings")
                Paths.QrCode, null -> {}
            }
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
                        },
                        modifier = Modifier.testTag(backButton)
                    )
                }
                Paths.Home, null -> {}
            }
        },
        actions = {
            when (currentPath) {
                Paths.Home -> {
                    IconButton(
                        onClick = { navController.navigate(Paths.QrCode.path) },
                        Modifier.testTag("qrcode")
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.qrcode),
                            contentDescription = null,
                        )
                    }
                    IconButton(
                        onClick = { navController.navigate(Paths.Settings.path) },
                        Modifier.testTag(settings)
                    ) {
                        Icon(
                            ImageBitmap.imageResource(R.drawable.ic_car_cog_black_24dp),
                            contentDescription = null,
                        )
                    }
                }
                Paths.Settings, Paths.QrCode, null -> {}
            }
        },
        modifier = modifier
    )
}

internal sealed class Paths(val path: String) {
    object Home : Paths("home")
    object QrCode : Paths("home/qrcode")
    object Settings : Paths("home/settings")

    companion object {
        fun from(string: String) = when (string) {
            "home" -> Home
            "home/qrcode" -> QrCode
            "home/settings" -> Settings
            else -> throw IllegalArgumentException("Unknown path $string")
        }
    }
}

public object HomeTags {
    public const val settings: String = "home_settings"
    public const val backButton: String = "back_button"
}
