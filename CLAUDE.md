# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Riichi Mahjong Calculator is a Kotlin Multiplatform app built with Compose Multiplatform that provides mahjong calculation tools including hand efficiency, winning hand analysis, and score calculation. The app supports Android, iOS, Desktop (JVM), and Web (WASM) platforms with AI-powered mahjong tile recognition.

## Build Configuration

This project uses a custom Gradle build system with modular conventions:

### Enable Platform Builds
Different platforms are controlled by environment properties in `env.properties`:
- `ENABLE_ANDROID=true/false` - Android app build
- `ENABLE_DESKTOP=true/false` - Desktop JVM app build  
- `ENABLE_WASM=true/false` - Web WASM app build
- `ENABLE_IOS=true/false` - iOS app build

By default, only Android is enabled unless properties are explicitly set.

### Common Build Commands

```bash
# Build Android app (debug)
./gradlew composeApp:assembleDebug

# Build Android app (release)  
./gradlew composeApp:assembleRelease

# Run tests
./gradlew test

# Build desktop app (requires ENABLE_DESKTOP=true in env.properties)
./gradlew desktopApp:run

# Build web app (requires ENABLE_WASM=true in env.properties)
./gradlew webApp:wasmJsBrowserDevelopmentRun
```

### Project Structure

The project follows a modular Kotlin Multiplatform structure:

- `shared/` - Core application logic, UI components, and screens (KMP)
- `base-components/` - Reusable UI components and utilities (KMP)
- `mahjong-detector/` - AI-powered mahjong tile recognition (KMP)
- `composeApp/` - Android application entry point
- `desktopApp/` - Desktop (JVM) application entry point
- `webApp/` - Web (WASM) application entry point
- `iosApp/` - iOS application (Xcode project)
- `kuikly-shared/` - Additional shared module using Kuikly framework
- `third-party/` - Vendored dependencies
- `build-logic/` - Custom Gradle convention plugins

### Source Set Organization

The project uses a sophisticated source set hierarchy:
- `commonMain` - Platform-agnostic code
- `androidMain` - Android-specific implementations
- `desktopMain` - Desktop JVM implementations  
- `iosMain` - iOS-specific implementations
- `wasmJsMain` - Web WASM implementations
- `mobileMain` - Shared mobile code (Android + iOS)
- `nonAndroidMain` - Non-Android platforms
- `desktopAndWasmJsMain` - Desktop and Web shared code

## Architecture

### Navigation & Screens
- Uses Voyager for navigation with URL-based routing
- Screen registry in `App.kt` maps paths to screen components
- Screens follow a consistent pattern: `XxxScreen`, `XxxScreenModel`, `XxxFormState`

### Key Screens
- `ShantenScreen` - Hand efficiency calculation
- `FuroShantenScreen` - Meld hand efficiency  
- `HoraScreen` - Winning hand analysis
- `HanhuScreen` - Score calculation by fan/minipoints
- `AboutScreen` - App information and licenses

### State Management
- Uses Compose state and ViewModels via Voyager's ScreenModel
- Form state classes manage input validation and UI state
- History tracking via DataStore for calculation results

### Mahjong Tile Recognition
- AI model integration for tile recognition from images
- Platform-specific implementations:
  - Android: TensorFlow Lite
  - Desktop: ONNX Runtime
  - iOS: Core ML
  - Web: TensorFlow.js

### UI Components
- Material 3 design system with custom theming
- Tile input components with keyboard and recognition support
- Responsive layouts using WindowSizeClass
- Capturable composables for screenshot functionality

## Development Notes

### Platform-Specific Features
- Image recognition optimized for Majsoul screenshots
- Clipboard integration for tile data
- File picker integration for image selection
- Platform-specific logging and error reporting (Sentry)

### Testing
- Unit tests in `commonTest` source sets
- Platform-specific tests in respective test source sets
- Use `./gradlew test` to run all tests

### Dependencies
- Core mahjong logic: `mahjong-utils` library
- UI: Compose Multiplatform with Material 3
- Navigation: Voyager
- Serialization: Kotlinx Serialization
- Async: Kotlinx Coroutines
- Data persistence: DataStore
- Image processing: Platform-specific ML frameworks

### Build System
- Custom Gradle convention plugins in `build-logic/`
- KmpPlugin configures multiplatform targets and source sets
- Platform-specific app plugins handle packaging and distribution
- Version catalog in `gradle/libs.versions.toml`