package com.mp3converter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mp3converter.ui.components.MetadataTextField
import com.mp3converter.ui.viewmodel.Mp3EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: Mp3EditorViewModel,
    onNavigateBack: () -> Unit
) {
    val metadata by viewModel.metadata.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Metadata",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Messages
            if (successMessage != null) {
                SuccessMessageBox(
                    message = successMessage ?: "",
                    onDismiss = { viewModel.clearMessages() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorMessage != null) {
                ErrorMessageBox(
                    message = errorMessage ?: "",
                    onDismiss = { viewModel.clearMessages() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Title Field
            MetadataTextField(
                label = "Title",
                value = metadata.title,
                onValueChange = { viewModel.updateTitle(it) }
            )

            // Artist Field
            MetadataTextField(
                label = "Artist",
                value = metadata.artist,
                onValueChange = { viewModel.updateArtist(it) }
            )

            // Album Field
            MetadataTextField(
                label = "Album",
                value = metadata.album,
                onValueChange = { viewModel.updateAlbum(it) }
            )

            // Genre Field
            MetadataTextField(
                label = "Genre",
                value = metadata.genre,
                onValueChange = { viewModel.updateGenre(it) }
            )

            // Year Field
            MetadataTextField(
                label = "Year",
                value = metadata.year,
                onValueChange = { viewModel.updateYear(it) }
            )

            // Comment Field
            MetadataTextField(
                label = "Comment",
                value = metadata.comment,
                onValueChange = { viewModel.updateComment(it) },
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    enabled = !isLoading,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 3.dp,
                        pressedElevation = 6.dp
                    )
                ) {
                    Text("Cancel", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.saveMetadata() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = !isLoading,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                        )
                    } else {
                        Text("Save Changes", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SuccessMessageBox(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.tertiaryContainer
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                message,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ErrorMessageBox(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                message,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
