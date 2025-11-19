package com.mp3converter.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mp3converter.data.Mp3Metadata
import com.mp3converter.utils.ImageHandler
import com.mp3converter.utils.Mp3Handler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Mp3EditorViewModel(context: Context) : ViewModel() {

    private val mp3Handler = Mp3Handler(context)
    private val imageHandler = ImageHandler(context)

    private val _metadata = MutableStateFlow(Mp3Metadata())
    val metadata: StateFlow<Mp3Metadata> = _metadata

    private val _audioFileUri = MutableStateFlow<Uri?>(null)
    val audioFileUri: StateFlow<Uri?> = _audioFileUri

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun setAudioFileUri(uri: Uri) {
        _audioFileUri.value = uri
        loadMetadata(uri)
    }

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
        // Convert image to byte array immediately
        imageHandler.imageUriToByteArray(uri)?.let { imageData ->
            val compressedImage = imageHandler.compressImage(imageData)
            updateMetadata(_metadata.value.copy(albumArtByteArray = compressedImage))
        }
    }

    private fun loadMetadata(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val loadedMetadata = mp3Handler.readMetadata(uri)
                if (loadedMetadata != null) {
                    _metadata.value = loadedMetadata
                } else {
                    _errorMessage.value = "Failed to load metadata"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMetadata(newMetadata: Mp3Metadata) {
        _metadata.value = newMetadata
    }

    fun updateTitle(title: String) {
        _metadata.value = _metadata.value.copy(title = title)
    }

    fun updateArtist(artist: String) {
        _metadata.value = _metadata.value.copy(artist = artist)
    }

    fun updateAlbum(album: String) {
        _metadata.value = _metadata.value.copy(album = album)
    }

    fun updateGenre(genre: String) {
        _metadata.value = _metadata.value.copy(genre = genre)
    }

    fun updateYear(year: String) {
        _metadata.value = _metadata.value.copy(year = year)
    }

    fun updateComment(comment: String) {
        _metadata.value = _metadata.value.copy(comment = comment)
    }

    fun saveMetadata() {
        val currentAudioUri = _audioFileUri.value
        if (currentAudioUri != null) {
            viewModelScope.launch(Dispatchers.IO) {
                _isLoading.value = true
                try {
                    val result = mp3Handler.writeMetadata(currentAudioUri, _metadata.value)
                    result.fold(
                        onSuccess = {
                            _successMessage.value = "Metadata saved successfully!"
                        },
                        onFailure = { error ->
                            _errorMessage.value = error.message ?: "Failed to save metadata. Please check storage permissions."
                        }
                    )
                } catch (e: Exception) {
                    _errorMessage.value = "Error: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _errorMessage.value = "No audio file selected"
        }
    }

    fun clearMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }

    fun reset() {
        _metadata.value = Mp3Metadata()
        _audioFileUri.value = null
        _imageUri.value = null
        _successMessage.value = null
        _errorMessage.value = null
    }
}
