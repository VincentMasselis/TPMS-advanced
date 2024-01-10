package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicle
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdown
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.LocalVehicleComponent
import com.masselis.tpmsadvanced.core.feature.ioc.VehicleComponent
import com.masselis.tpmsadvanced.core.ui.LocalHomeNavController
import com.masselis.tpmsadvanced.core.ui.Spotlight
import com.masselis.tpmsadvanced.feature.background.interfaces.ui.ManualBackgroundIconButton
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags.backButton
import com.masselis.tpmsadvanced.interfaces.ioc.AppPhoneComponent.Companion.HomeViewModel
import com.masselis.tpmsadvanced.interfaces.ioc.AppPhoneComponent.Companion.VehicleHomeViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.VehicleHomeViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.VehicleHomeViewModel.SpotlightEvent
import com.masselis.tpmsadvanced.qrcode.interfaces.QrCodeScan
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.UnlocatedSensorList
import java.util.UUID

@Composable
internal fun Home(
    expectedVehicle: UUID?,
    viewModel: HomeViewModel = viewModel(key = "HomeViewModel_$expectedVehicle") {
        HomeViewModel(expectedVehicle)
    }
) {
    val vehicleComponent by viewModel.vehicleComponentStateFlow.collectAsState()
    VehicleHome(
        vehicleComponent = vehicleComponent
    )
}

@Suppress("LongMethod")
@Composable
internal fun VehicleHome(
    vehicleComponent: VehicleComponent,
    viewModel: VehicleHomeViewModel = viewModel { VehicleHomeViewModel() }
) {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalVehicleComponent provides vehicleComponent,
        LocalHomeNavController provides navController
    ) {
        var offsetToFocus by remember { mutableStateOf<Offset?>(null) }
        var showManualMonitoringSpotlight by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    manualBackgroundButtonModifier = Modifier.onGloballyPositioned { coordinates ->
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
                    navController = navController,
                    startDestination = "${Path.Home(vehicleComponent.vehicle.uuid)}"
                ) {
                    val modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                    composable(route = "${Path.Home(vehicleComponent.vehicle.uuid)}") {
                        CurrentVehicle(modifier = modifier)
                    }
                    composable("${Path.Settings(vehicleComponent.vehicle.uuid)}") {
                        Settings(modifier = modifier)
                    }
                    composable("${Path.BindingMethod(vehicleComponent.vehicle.uuid)}") {
                        ChooseBindingMethod(
                            scanQrCode = {
                                navController.navigate("${Path.QrCode(vehicleComponent.vehicle.uuid)}")
                            },
                            searchUnlocatedSensors = {
                                navController.navigate("${Path.Unlocated(vehicleComponent.vehicle.uuid)}")
                            },
                            modifier = modifier
                        )
                    }
                    composable("${Path.QrCode(vehicleComponent.vehicle.uuid)}") {
                        QrCodeScan(modifier = modifier)
                    }
                    composable("${Path.Unlocated(vehicleComponent.vehicle.uuid)}") {
                        UnlocatedSensorList(
                            vehicleUuid = vehicleComponent.vehicle.uuid,
                            bindingFinished = {
                                navController.popBackStack(
                                    "${Path.Home(vehicleComponent.vehicle.uuid)}",
                                    false
                                )
                            },
                            modifier = modifier
                        )
                    }
                }
            }
        )
        if (offsetToFocus != null)
            AnimatedVisibility(
                visible = showManualMonitoringSpotlight,
                enter = fadeIn(spring(0f)),
                exit = fadeOut()
            ) {
                with(LocalDensity.current) {
                    @Suppress("MaxLineLength")
                    Spotlight(
                        center = offsetToFocus!!,
                        radius = 50.dp.toPx(),
                        text = AnnotatedString("Something new is waiting for you,\nTap this button to monitor your vehicle while the app is in background"),
                        textPadding = 8.dp.toPx(),
                        textStyle = TextStyle.Default.copy(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        onSpotlight = { showManualMonitoringSpotlight = false }
                    )
                }
            }
        LaunchedEffect(viewModel) {
            for (event in viewModel.eventChannel) {
                when (event) {
                    SpotlightEvent.ManualMonitorDropdown ->
                        showManualMonitoringSpotlight = true
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
    manualBackgroundButtonModifier: Modifier = Modifier
) {
    val navController = LocalHomeNavController.current
    val currentPath = navController.currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route
        ?.let { Path.from(it) }
    CenterAlignedTopAppBar(
        title = {
            when (currentPath) {
                is Path.Home -> CurrentVehicleDropdown()
                is Path.Settings -> Text(text = "Settings")
                is Path.BindingMethod -> Text(text = "Binding method")
                is Path.Unlocated -> Text(text = "Binding")
                is Path.QrCode, null -> {}
            }
        },
        navigationIcon = {
            when (currentPath) {
                is Path.Settings, is Path.BindingMethod, is Path.QrCode, is Path.Unlocated -> {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        content = {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go back"
                            )
                        },
                        modifier = Modifier.testTag(backButton)
                    )
                }

                is Path.Home, null -> {}
            }
        },
        actions = {
            var showMenu by remember { mutableStateOf(false) }
            when (currentPath) {
                is Path.Home -> {
                    ManualBackgroundIconButton(
                        modifier = manualBackgroundButtonModifier
                            .testTag(HomeTags.Actions.manualBackground)
                    )
                    IconButton(
                        onClick = { showMenu = true },
                        Modifier.testTag(HomeTags.Actions.overflow)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Show more options",
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.testTag(HomeTags.Actions.Overflow.name)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Bind sensors") },
                            onClick = {
                                showMenu = false
                                navController.navigate("${Path.BindingMethod(currentPath.vehicleUUID)}")
                            },
                            modifier = Modifier.testTag(HomeTags.Actions.Overflow.bindingMethod)
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                navController.navigate("${Path.Settings(currentPath.vehicleUUID)}")
                            },
                            modifier = Modifier.testTag(HomeTags.Actions.Overflow.settings)
                        )
                    }
                }

                is Path.Settings, is Path.BindingMethod, is Path.QrCode, is Path.Unlocated, null -> {}
            }
        },
        modifier = modifier
    )
}

public object HomeTags {
    public object Actions {
        public const val manualBackground: String = "put_in_manual_background"
        public const val overflow: String = "overflow_menu_icon"

        public object Overflow {
            public const val name: String = "overflow_menu"
            public const val bindingMethod: String = "bindingMethod"
            public const val settings: String = "action_settings"
        }
    }

    public const val backButton: String = "back_button"
}
