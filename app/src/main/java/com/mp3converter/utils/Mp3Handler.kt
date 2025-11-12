package com.mp3converter.utils

import android.content.Context
import android.net.Uri
import com.mp3converter.data.Mp3Metadata
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.Artwork
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
    fun writeMetadata(uri: Uri, metadata: Mp3Metadata): Boolean = try {
        val file = getFileFromUri(uri)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tag ?: audioFile.createDefaultTag()

        tag.setField(FieldKey.TITLE, metadata.title)
        tag.setField(FieldKey.ARTIST, metadata.artist)
        tag.setField(FieldKey.ALBUM, metadata.album)
        tag.setField(FieldKey.GENRE, metadata.genre)
        tag.setField(FieldKey.YEAR, metadata.year)
        tag.setField(FieldKey.COMMENT, metadata.comment)

        // Add album art if present
        metadata.albumArtByteArray?.let { imageData ->
            val artwork = Artwork()
            artwork.binaryData = imageData
            artwork.mimeType = "image/jpeg"
            tag.deleteArtworkField()
            tag.setField(artwork)
        }

        audioFile.commit()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
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
