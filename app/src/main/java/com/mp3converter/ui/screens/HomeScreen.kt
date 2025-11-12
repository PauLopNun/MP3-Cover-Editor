package com.mp3converter.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mp3converter.ui.components.AlbumArtDisplay
import com.mp3converter.ui.components.FilePickerButton
import com.mp3converter.ui.viewmodel.Mp3EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: Mp3EditorViewModel,
    context: Context,
    onNavigateToEditor: () -> Unit
) {
    val metadata by viewModel.metadata.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val audioFileUri by viewModel.audioFileUri.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Audio file picker with write permissions
    val audioFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable URI permissions for read and write
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewModel.setAudioFileUri(it)
        }
    }

    // Image file picker
    val imageFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setImageUri(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "MP3 Metadata Editor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Messages
            if (successMessage != null) {
                MessageBox(
                    message = successMessage ?: "",
                    isSuccess = true,
                    onDismiss = { viewModel.clearMessages() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorMessage != null) {
                MessageBox(
                    message = errorMessage ?: "",
                    isSuccess = false,
                    onDismiss = { viewModel.clearMessages() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Album Art Display with card
            androidx.compose.material3.Card(
                modifier = Modifier
                    .padding(16.dp),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(8.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (metadata.albumArtByteArray != null) {
                        val bitmap = android.graphics.BitmapFactory.decodeByteArray(
                            metadata.albumArtByteArray,
                            0,
                            metadata.albumArtByteArray!!.size
                        )
                        AlbumArtDisplay(bitmap = bitmap, size = 200)
                    } else {
                        AlbumArtDisplay(bitmap = null, size = 200)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // File Selection Section
            Text(
                "Step 1: Select Files",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilePickerButton(
                    text = "Select MP3",
                    onClick = { audioFilePicker.launch(arrayOf("audio/mpeg", "audio/*")) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )

                FilePickerButton(
                    text = "Select Image",
                    onClick = { imageFilePicker.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
            }

            // Selected Files Info
            if (audioFileUri != null) {
                InfoBox(
                    title = "Audio File Selected",
                    value = "MP3 file loaded",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            if (imageUri != null) {
                InfoBox(
                    title = "Image Selected",
                    value = "Cover image loaded",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            // Current Metadata Preview
            if (audioFileUri != null) {
                Text(
                    "Step 2: Edit Metadata",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp, top = 16.dp)
                )

                MetadataPreviewBox(metadata = metadata)

                Spacer(modifier = Modifier.height(24.dp))

                // Edit Button
                Button(
                    onClick = onNavigateToEditor,
                    modifier = Modifier
                        .fillMaxWidth()
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
                        Text("Edit Metadata", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "Please select an MP3 file to begin",
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.tertiaryContainer
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                title,
                fontSize = 12.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
private fun MessageBox(
    message: String,
    isSuccess: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSuccess)
                androidx.compose.material3.MaterialTheme.colorScheme.tertiaryContainer
            else
                androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSuccess)
                    androidx.compose.material3.MaterialTheme.colorScheme.onTertiaryContainer
                else
                    androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun MetadataPreviewBox(metadata: com.mp3converter.data.Mp3Metadata) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            PreviewRow("Title", metadata.title)
            PreviewRow("Artist", metadata.artist)
            PreviewRow("Album", metadata.album)
            PreviewRow("Genre", metadata.genre)
            PreviewRow("Year", metadata.year)
        }
    }
}

@Composable
private fun PreviewRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            label,
            fontSize = 12.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            value.ifEmpty { "Not set" },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
        )
    }
}
