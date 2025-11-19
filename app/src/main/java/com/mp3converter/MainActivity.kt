package com.mp3converter

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mp3converter.ui.screens.HomeScreen
import com.mp3converter.ui.screens.EditorScreen
import com.mp3converter.ui.screens.PermissionScreen
import com.mp3converter.ui.viewmodel.Mp3EditorViewModel
import com.mp3converter.ui.theme.Mp3ConverterTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mp3ConverterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Request storage permissions based on Android version
                    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        listOf(
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    } else {
                        listOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    }

                    val permissionsState = rememberMultiplePermissionsState(permissions)

                    LaunchedEffect(Unit) {
                        if (!permissionsState.allPermissionsGranted) {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    }

                    if (permissionsState.allPermissionsGranted) {
                        AppNavigation(context = this)
                    } else {
                        PermissionScreen(
                            permissionsState = permissionsState,
                            onRequestPermissions = {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(context: android.content.Context) {
    val currentScreen = remember { mutableStateOf<String>("home") }
    val viewModel = remember { Mp3EditorViewModel(context) }

    when (currentScreen.value) {
        "home" -> {
            HomeScreen(
                viewModel = viewModel,
                context = context,
                onNavigateToEditor = { currentScreen.value = "editor" }
            )
        }
        "editor" -> {
            EditorScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen.value = "home" }
            )
        }
    }
}
