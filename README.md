# MP3 Metadata Editor

A simple and functional Android application built with **Kotlin** and **Jetpack Compose** that allows users to edit MP3 metadata and add album artwork to their audio files.

## Features

- **Select MP3 Files**: Pick any MP3 audio file from your device storage
- **Select Album Art**: Choose an image to use as the album cover
- **Edit Metadata**: Modify the following metadata fields:
  - Title
  - Artist
  - Album
  - Genre
  - Year
  - Comment
- **Apply Album Artwork**: Embed the selected image directly into the MP3 file as album art
- **Save Changes**: Write all changes back to the MP3 file
- **Preview Album Art**: View the album artwork before and after applying changes
- **Intuitive UI**: Clean and user-friendly interface built with Material Design 3

## Project Structure

```
mp3converter/
├── app/
│   ├── src/main/
│   │   ├── java/com/mp3converter/
│   │   │   ├── MainActivity.kt                 # Main activity and navigation
│   │   │   ├── data/
│   │   │   │   └── Mp3Metadata.kt            # Data class for metadata
│   │   │   ├── utils/
│   │   │   │   ├── Mp3Handler.kt             # MP3 file operations
│   │   │   │   └── ImageHandler.kt           # Image file operations
│   │   │   └── ui/
│   │   │       ├── screens/
│   │   │       │   ├── HomeScreen.kt         # Main home screen
│   │   │       │   └── EditorScreen.kt       # Metadata editor screen
│   │   │       ├── components/
│   │   │       │   ├── FilePickerButton.kt   # File picker button component
│   │   │       │   ├── MetadataTextField.kt  # Text field for metadata
│   │   │       │   └── AlbumArtDisplay.kt    # Album art preview
│   │   │       └── theme/
│   │   │           ├── Theme.kt              # App theme configuration
│   │   │           └── Type.kt               # Typography styles
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   │       └── values/
│   │           ├── strings.xml               # String resources
│   │           ├── themes.xml                # Theme resources
│   │           └── xml/
│   │               ├── data_extraction_rules.xml
│   │               └── backup_rules.xml
│   ├── build.gradle.kts                      # App module gradle build
│   └── proguard-rules.pro                    # ProGuard rules
├── build.gradle.kts                          # Root gradle build
├── settings.gradle.kts                       # Gradle settings
└── README.md                                 # This file
```

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Android API**: Level 26+ (Android 8.0+)
- **MP3 Metadata Library**: jaudiotagger 2.2.3
- **Architecture**: MVVM with Jetpack Compose

## Dependencies

### Core Android
- `androidx.core:core-ktx:1.12.0`
- `androidx.lifecycle:lifecycle-runtime-ktx:2.6.1`
- `androidx.activity:activity-compose:1.8.0`

### Jetpack Compose
- `androidx.compose.ui:ui:1.5.4`
- `androidx.compose.material3:material3:1.1.1`
- `androidx.compose.material:material-icons-extended:1.5.4`

### File Handling
- `androidx.documentfile:documentfile:1.0.1`

### MP3 Metadata
- `org.jaudiotagger:jaudiotagger:2.2.3`

### Permissions
- `com.google.accompanist:accompanist-permissions:0.33.2-alpha`

## Installation & Setup

### Prerequisites
- Android Studio Giraffe or newer
- Kotlin plugin
- Gradle 8.0 or higher
- JDK 8 or higher

### Building the Project

1. Clone the repository:
```bash
git clone <repository-url>
cd mp3converter
```

2. Open the project in Android Studio

3. Sync Gradle files:
   - Click "Sync Now" when prompted
   - Wait for the sync to complete

4. Build the project:
   - Select Build > Make Project
   - Or press Ctrl+F9

5. Run on emulator or device:
   - Click Run > Run 'app'
   - Select your target device/emulator

## Usage Guide

### Step 1: Select Files
1. Launch the application
2. Click **"Select MP3"** to choose an MP3 file from your device storage
3. Click **"Select Image"** to choose an image for the album cover

### Step 2: View Metadata
- The current metadata from the MP3 file will be displayed on the home screen
- The album artwork preview will show the selected image (if any)

### Step 3: Edit Metadata
1. Click **"Edit Metadata"** to navigate to the editor screen
2. Modify the following fields as desired:
   - **Title**: Song name
   - **Artist**: Artist/performer name
   - **Album**: Album name
   - **Genre**: Music genre
   - **Year**: Release year
   - **Comment**: Additional comments

### Step 4: Save Changes
1. Click **"Save"** to apply all changes to the MP3 file
2. A success or error message will appear
3. Click **"Cancel"** to discard changes and return to home

## Permissions

The application requires the following permissions:
- `READ_EXTERNAL_STORAGE`: To read MP3 and image files
- `WRITE_EXTERNAL_STORAGE`: To write metadata to MP3 files
- `MANAGE_EXTERNAL_STORAGE`: For full file system access (Android 11+)

These permissions are declared in `AndroidManifest.xml` and will be requested at runtime on Android 6.0+.

## Features in Detail

### MP3 Metadata Handling
- **Reading**: Extracts title, artist, album, genre, year, comment, and album artwork from MP3 files
- **Writing**: Saves all metadata changes and applies album artwork to the MP3 file
- **Album Art**: Automatically compresses images to optimize file size

### Image Processing
- **Format Support**: JPEG, PNG, and other common image formats
- **Compression**: Images are compressed to 85% quality to reduce MP3 file size
- **Preview**: Real-time preview of album artwork before and after changes

### UI/UX
- **Material Design 3**: Modern, clean interface with Material Design components
- **Responsive Layout**: Works on various screen sizes and orientations
- **Status Messages**: Real-time feedback for success and error conditions
- **Loading States**: Visual indicators during file operations

## Known Limitations

- The application uses the cache directory to temporarily process content:// URIs
- Large image files are compressed to optimize MP3 file size
- The application is designed for single-file operations (one MP3 at a time)

## Future Enhancements

- Batch processing multiple MP3 files
- Support for other audio formats (FLAC, M4A, etc.)
- Album art cropping and editing
- Lyrics editing
- Audio file details display (bitrate, duration, etc.)
- Undo/Redo functionality
- File manager integration

## Troubleshooting

### Permission Issues
- If file picker doesn't work, ensure the app has storage permissions
- On Android 11+, check if the app has "All Files Access" permission

### Cannot Read/Write MP3 Files
- Verify the file is a valid MP3 format
- Check file permissions on your device
- Ensure the MP3 file is not corrupted

### Album Art Not Appearing
- Verify you've selected a valid image file
- Try compressing the image to a smaller size
- Check if your MP3 player app supports embedded album art

### Application Crashes
- Check Android Studio logcat for error messages
- Ensure all dependencies are correctly synced
- Verify the target Android API level (minimum 26)

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is provided as-is for educational and personal use.

## Version

- **Current Version**: 1.0.0
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## Contact & Support

For issues or questions about the application, please check the troubleshooting section above or review the code comments for additional details.

---

**Made with ❤️ in Kotlin & Jetpack Compose**
