package com.mp3converter.utils

import android.content.Context
import android.net.Uri
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
    fun writeMetadata(uri: Uri, metadata: Mp3Metadata): Boolean {
        var tempFile: File? = null
        var tempArtworkFile: File? = null

        return try {
            // Step 1: Copy the original file to a temporary file
            tempFile = File(context.cacheDir, "temp_audio_edit_${System.currentTimeMillis()}.mp3")

            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot open input stream for URI: $uri")

            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if (!tempFile.exists() || tempFile.length() == 0L) {
                throw Exception("Failed to copy file to temporary location")
            }

            // Step 2: Modify the temporary file with jaudiotagger
            val audioFile = AudioFileIO.read(tempFile)
            val tag = audioFile.tag ?: audioFile.createDefaultTag()

            tag.setField(FieldKey.TITLE, metadata.title)
            tag.setField(FieldKey.ARTIST, metadata.artist)
            tag.setField(FieldKey.ALBUM, metadata.album)
            tag.setField(FieldKey.GENRE, metadata.genre)
            tag.setField(FieldKey.YEAR, metadata.year)
            tag.setField(FieldKey.COMMENT, metadata.comment)

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
                ?: throw Exception("Cannot open output stream for URI: $uri")

            outputStream.use { output ->
                tempFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("Mp3Handler", "Error writing metadata: ${e.message}", e)
            false
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
}
