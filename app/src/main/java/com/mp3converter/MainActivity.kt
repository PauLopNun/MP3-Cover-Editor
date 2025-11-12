package com.mp3converter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mp3converter.ui.screens.HomeScreen
import com.mp3converter.ui.screens.EditorScreen
import com.mp3converter.ui.viewmodel.Mp3EditorViewModel
import com.mp3converter.ui.theme.Mp3ConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mp3ConverterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(context = this)
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
