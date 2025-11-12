# Release Guide for MP3 Cover Editor v1.0

## Prerequisites

1. All changes have been committed to the main branch
2. The app has been tested and is working correctly
3. You have push access to the repository

## Steps to Create Release v1.0

### Option 1: Using GitHub UI (Recommended)

1. **Commit and push all changes:**
   ```bash
   git add .
   git commit -m "Release v1.0.0"
   git push origin main
   ```

2. **Create a new tag:**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

3. **GitHub Actions will automatically:**
   - Build the release APK
   - Create a GitHub release
   - Upload the APK to the release

4. **Access the release:**
   - Go to: https://github.com/yourusername/MP3-Cover-Editor/releases
   - The v1.0.0 release will be available with the APK

### Option 2: Manual Release

If you prefer to build locally:

1. **Build the release APK:**
   ```bash
   ./gradlew assembleRelease
   ```

2. **Find the APK at:**
   ```
   app/build/outputs/apk/release/app-release-unsigned.apk
   ```

3. **Create release manually on GitHub:**
   - Go to: https://github.com/yourusername/MP3-Cover-Editor/releases/new
   - Tag version: `v1.0.0`
   - Release title: `MP3 Cover Editor v1.0.0`
   - Description: Add release notes (features, fixes, etc.)
   - Upload the APK file
   - Click "Publish release"

## Release Notes Template

```markdown
# MP3 Cover Editor v1.0.0

## Features

- **Edit MP3 Metadata**: Modify title, artist, album, genre, year, and comments
- **Album Cover Art**: Add and embed album artwork directly into MP3 files
- **File Selection**: Browse and select MP3 files and images from device storage
- **Preview**: View album artwork before and after changes
- **Modern UI**: Clean Material Design 3 interface with intuitive navigation
- **Persistent Permissions**: Uses Storage Access Framework for reliable file access

## Technical Details

- **Min SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **Built with**: Kotlin, Jetpack Compose
- **Metadata Library**: jaudiotagger 3.0.1

## Installation

1. Download the APK file below
2. Enable "Install from Unknown Sources" on your device
3. Install the APK
4. Grant storage permissions when prompted

## Known Issues

- Files must be re-selected after app updates to maintain write permissions
- Only supports single file operations (no batch processing yet)

## What's Next

See our [roadmap](https://github.com/yourusername/MP3-Cover-Editor#future-enhancements) for upcoming features!
```

## Post-Release Checklist

- [ ] Verify the APK downloads correctly
- [ ] Test installation on a real device
- [ ] Update README.md with correct release link
- [ ] Share the release announcement
- [ ] Monitor for bug reports and user feedback

## Troubleshooting

### GitHub Actions fails to build
- Check the workflow logs in the Actions tab
- Ensure all dependencies are correctly specified
- Verify the Gradle wrapper is committed

### APK is not uploaded to release
- Check that the tag was pushed correctly: `git tag -l`
- Verify GitHub Actions workflow completed successfully
- Check that GITHUB_TOKEN has proper permissions

## Version Numbering

We follow semantic versioning: MAJOR.MINOR.PATCH
- **MAJOR**: Breaking changes
- **MINOR**: New features (backwards compatible)
- **PATCH**: Bug fixes

Next versions:
- v1.0.1 - Bug fixes
- v1.1.0 - New features
- v2.0.0 - Major changes

---

**Ready to release?** Follow the steps above and let's ship v1.0! 🚀
