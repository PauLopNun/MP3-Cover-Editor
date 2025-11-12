package com.mp3converter.data

data class Mp3Metadata(
    var title: String = "",
    var artist: String = "",
    var album: String = "",
    var genre: String = "",
    var year: String = "",
    var comment: String = "",
    var albumArtByteArray: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mp3Metadata

        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (genre != other.genre) return false
        if (year != other.year) return false
        if (comment != other.comment) return false
        if (albumArtByteArray != null) {
            if (other.albumArtByteArray == null) return false
            if (!albumArtByteArray.contentEquals(other.albumArtByteArray)) return false
        } else if (other.albumArtByteArray != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + genre.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + (albumArtByteArray?.contentHashCode() ?: 0)
        return result
    }
}
