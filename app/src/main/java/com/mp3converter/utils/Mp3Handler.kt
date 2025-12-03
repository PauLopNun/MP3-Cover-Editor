package com.mp3converter.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import com.mp3converter.data.Mp3Metadata
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.AndroidArtwork
import java.io.File

class Mp3Handler(private val context: Context) {

    /**
     * Reads metadata from an MP3 file
     */
    fun readMetadata(uri: Uri): Mp3Metadata? = try {
        val file = getFileFromUri(uri)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tag

        val metadata = Mp3Metadata(
            title = tag?.getFirst(FieldKey.TITLE) ?: "",
            artist = tag?.getFirst(FieldKey.ARTIST) ?: "",
            album = tag?.getFirst(FieldKey.ALBUM) ?: "",
            genre = tag?.getFirst(FieldKey.GENRE) ?: "",
            year = tag?.getFirst(FieldKey.YEAR) ?: "",
            comment = tag?.getFirst(FieldKey.COMMENT) ?: ""
        )

        // Extract album art
        tag?.artworkList?.firstOrNull()?.let { artwork ->
            metadata.albumArtByteArray = artwork.binaryData
        }

        metadata
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    /**
     * Writes metadata to an MP3 file
     */
    fun writeMetadata(uri: Uri, metadata: Mp3Metadata): Result<Boolean> {
        var tempFile: File? = null
        var tempArtworkFile: File? = null

        return try {
            // Step 1: Copy the original file to a temporary file
            tempFile = File(context.cacheDir, "temp_audio_edit_${System.currentTimeMillis()}.mp3")

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Cannot read file. Please check storage permissions."))

            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if (!tempFile.exists() || tempFile.length() == 0L) {
                return Result.failure(Exception("Failed to copy file to temporary location. Storage may be full or permissions missing."))
            }

            // Step 2: Modify the temporary file with jaudiotagger
            val audioFile = AudioFileIO.read(tempFile)
            val tag = audioFile.tag ?: audioFile.createDefaultTag()

            // Only set fields if they're not empty to avoid null/empty field errors
            try {
                if (metadata.title.isNotEmpty()) {
                    tag.setField(FieldKey.TITLE, metadata.title)
                } else {
                    tag.deleteField(FieldKey.TITLE)
                }

                if (metadata.artist.isNotEmpty()) {
                    tag.setField(FieldKey.ARTIST, metadata.artist)
                } else {
                    tag.deleteField(FieldKey.ARTIST)
                }

                if (metadata.album.isNotEmpty()) {
                    tag.setField(FieldKey.ALBUM, metadata.album)
                } else {
                    tag.deleteField(FieldKey.ALBUM)
                }

                if (metadata.genre.isNotEmpty()) {
                    tag.setField(FieldKey.GENRE, metadata.genre)
                } else {
                    tag.deleteField(FieldKey.GENRE)
                }

                if (metadata.year.isNotEmpty()) {
                    tag.setField(FieldKey.YEAR, metadata.year)
                } else {
                    tag.deleteField(FieldKey.YEAR)
                }

                if (metadata.comment.isNotEmpty()) {
                    tag.setField(FieldKey.COMMENT, metadata.comment)
                } else {
                    tag.deleteField(FieldKey.COMMENT)
                }
            } catch (e: Exception) {
                android.util.Log.w("Mp3Handler", "Warning setting fields: ${e.message}")
                // Continue even if some fields fail
            }

            // Add album art if present
            metadata.albumArtByteArray?.let { imageData ->
                tempArtworkFile = File(context.cacheDir, "temp_artwork_${System.currentTimeMillis()}.jpg")
                tempArtworkFile?.writeBytes(imageData)
                val artwork = AndroidArtwork.createArtworkFromFile(tempArtworkFile)
                tag.deleteArtworkField()
                tag.addField(artwork)
            }

            // Commit changes to the temporary file
            audioFile.commit()

            // Step 3: Copy the modified temporary file back to the original URI
            val outputStream = context.contentResolver.openOutputStream(uri)
                ?: return Result.failure(Exception("Cannot write to file. Please check storage permissions."))

            outputStream.use { output ->
                tempFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            // Step 4: Notify MediaStore and music players about the changes
            notifyMediaStoreUpdate(uri)

            Result.success(true)
        } catch (e: SecurityException) {
            android.util.Log.e("Mp3Handler", "Permission error writing metadata: ${e.message}", e)
            Result.failure(Exception("Permission denied. Please grant storage permissions in app settings."))
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("Mp3Handler", "Error writing metadata: ${e.message}", e)
            Result.failure(Exception("Failed to save metadata: ${e.message}"))
        } finally {
            // Clean up temporary files
            try {
                tempFile?.delete()
                tempArtworkFile?.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Gets a File object from a URI
     */
    private fun getFileFromUri(uri: Uri): File {
        val filePath = when {
            uri.scheme == "file" -> uri.path ?: ""
            else -> {
                // For content:// URIs, we need to copy to a temporary file
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "temp_audio.mp3")
                inputStream?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                tempFile.absolutePath
            }
        }
        return File(filePath)
    }

    /**
     * Notifies the MediaStore that a file has been updated
     * This ensures music players and gallery apps see the latest changes
     */
    private fun notifyMediaStoreUpdate(uri: Uri) {
        try {
            // Method 1: Notify ContentResolver about the change
            context.contentResolver.notifyChange(uri, null)

            // Method 2: Try to get the real file path and scan it
            val filePath = when {
                uri.scheme == "content" -> {
                    // Try to get the actual file path from the URI
                    val projection = arrayOf(MediaStore.MediaColumns.DATA)
                    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                            cursor.getString(columnIndex)
                        } else null
                    }
                }
                uri.scheme == "file" -> uri.path
                else -> null
            }

            // Method 3: Use MediaScannerConnection to force a rescan
            filePath?.let { path ->
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(path),
                    arrayOf("audio/mpeg", "audio/mp3")
                ) { scannedPath, scannedUri ->
                    android.util.Log.d("Mp3Handler", "MediaStore updated for: $scannedPath")
                }
            } ?: run {
                // If we couldn't get the file path, at least notify with the URI
                android.util.Log.d("Mp3Handler", "Notified MediaStore about changes to: $uri")
            }
        } catch (e: Exception) {
            // Don't fail the save operation if notification fails
            android.util.Log.w("Mp3Handler", "Failed to notify MediaStore: ${e.message}")
        }
    }
}
