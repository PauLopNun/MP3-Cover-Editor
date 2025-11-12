package com.mp3converter.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.MaterialTheme
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

    // Audio file picker
    val audioFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setAudioFileUri(it) }
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
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White
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

            // Album Art Display
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

            Spacer(modifier = Modifier.height(24.dp))

            // File Selection Section
            Text(
                "Step 1: Select Files",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilePickerButton(
                    text = "Select MP3",
                    onClick = { audioFilePicker.launch("audio/mpeg") },
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 16.dp)
                )

                MetadataPreviewBox(metadata = metadata)

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Button
                Button(
                    onClick = onNavigateToEditor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                        )
                    } else {
                        Text("Edit Metadata", fontSize = 16.sp)
                    }
                }
            } else {
                Text(
                    "Please select an MP3 file to begin",
                    modifier = Modifier.padding(top = 32.dp),
                    color = Color.Gray
                )
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(
                color = Color(0xFFE8F5E9),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(title, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MessageBox(
    message: String,
    isSuccess: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(
                color = if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            message,
            fontSize = 14.sp,
            color = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
    }
}

@Composable
private fun MetadataPreviewBox(metadata: com.mp3converter.data.Mp3Metadata) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F5F5),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        PreviewRow("Title", metadata.title)
        PreviewRow("Artist", metadata.artist)
        PreviewRow("Album", metadata.album)
        PreviewRow("Genre", metadata.genre)
        PreviewRow("Year", metadata.year)
    }
}

@Composable
private fun PreviewRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(
            value.ifEmpty { "Not set" },
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
