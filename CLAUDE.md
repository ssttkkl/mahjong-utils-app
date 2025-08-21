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
- `base-components/` - Reusable UI components (KMP)
- `base-utils/` - Base utilities and platform abstractions (KMP)
- `mahjong-detector/` - AI-powered mahjong tile recognition (KMP)
- `composeApp/` - Android application entry point
- `desktopApp/` - Desktop (JVM) application entry point
- `webApp/` - Web (WASM) application entry point
- `iosApp/` - iOS application (Xcode project)
- `kuikly-shared/` - Shared Kuikly framework module (KMP)
- `kuikly-android/` - Android-specific Kuikly framework integration
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
- Kuikly UI framework integration for cross-platform native rendering
- Tile input components with keyboard and recognition support
- Responsive layouts using WindowSizeClass
- Capturable composables for screenshot functionality

### Kuikly UI Framework
This project integrates the [Kuikly UI framework](https://github.com/Tencent-TDS/KuiklyUI), a Kotlin Multiplatform cross-platform UI framework that provides:

**📚 Documentation**: Complete Kuikly UI documentation is available in `docs/kuikly-ui/` directory, including API references, examples, and guides. Since Kuikly UI is a relatively new framework, local documentation provides the most up-to-date information.

#### Core Features
- **Cross-platform native rendering**: Unlike Compose Multiplatform's Skia rendering, Kuikly uses native platform components for optimal performance
- **Platform support**: Android, iOS, HarmonyOS, H5, WeChat Mini Programs, and Desktop
- **Dynamic capabilities**: Hot updates and dynamic content delivery
- **Lightweight**: Smaller bundle size compared to Skia-based solutions
- **FlexBox layout**: CSS FlexBox-based layout system for consistent cross-platform layouts

#### Architecture
- **KuiklyUI Core**: Cross-platform high-level components, animations, gestures, and layouts
- **KuiklyBase**: Cross-platform infrastructure including networking, storage, threading, and debugging tools
- **Dual DSL Support**: 
  - Native Kuikly DSL for maximum performance and features
  - **Compose DSL compatibility** (Beta) for easier migration and standard Compose syntax

#### Key Components
- **View**: Basic container component (maps to FrameLayout on Android, UIView on iOS)
- **Text**: Text display with rich formatting options (colors, fonts, alignment, shadows, etc.)
- **Button**: Interactive button with customizable text and image attributes
- **Image**: Image display with various resize modes and loading states
- **List**: Scrollable list containers with performance optimizations
- **Input**: Text input components with validation and styling
- **Layout containers**: Flexible layout system based on FlexBox

#### Event System
- Touch events: `touchUp`, `touchDown`, `touchMove`, `touchCancel`
- Gesture events: `click`, `doubleClick`, `longPress`, `pan`
- Lifecycle events: `willAppear`, `didAppear`, `willDisappear`, `didDisappear`
- Layout events: `layoutFrameDidChange`, `animationCompletion`
- Visibility tracking: `appearPercentage`

#### Styling System
- **Basic attributes**: `backgroundColor`, `borderRadius`, `boxShadow`, `opacity`, `visibility`
- **Transform**: `rotate`, `scale`, `translate` with configurable anchor points
- **Layout**: `width`, `height`, `margin`, `padding`, `flexDirection`, `justifyContent`, `alignItems`
- **Position**: Relative and absolute positioning with `left`, `top`, `right`, `bottom`
- **Advanced**: Linear gradients, custom fonts, z-index layering

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
- UI frameworks: Compose Multiplatform with Material 3, Kuikly UI framework
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

## Coding Style Guidelines

### UI Development Priority Order

#### For kuikly-shared/ and kuikly-android/ modules:
1. **First choice**: Use Kuikly Compose DSL with `com.tencent.kuikly.compose.*` imports (see supported components below)
2. **Second choice**: Use native Kuikly DSL for features not available in Compose DSL
3. **Last resort**: Use platform-specific implementations only when necessary

#### For all other modules (shared/, base-components/, base-utils/, composeApp/, desktopApp/, webApp/, iosApp/):
1. **Use standard Compose Multiplatform** with `androidx.compose.*` imports
2. **Do NOT use Kuikly** - it's only for the specific kuikly modules
3. Follow standard Compose Multiplatform patterns and best practices

### Kuikly UI Framework Usage
- **IMPORTANT**: Kuikly framework is **ONLY used in specific modules**:
  - `kuikly-shared/` - Shared Kuikly framework module (KMP)
  - `kuikly-android/` - Android-specific Kuikly framework integration
- **All other modules** (`shared/`, `base-components/`, `base-utils/`, `composeApp/`, `desktopApp/`, `webApp/`, `iosApp/`) use **standard Compose Multiplatform** with `androidx.compose.*` imports
- **Module-specific rules**:
  - In `kuikly-shared/` and `kuikly-android/`: Use Kuikly Compose DSL with `com.tencent.kuikly.compose.*` imports
  - In all other modules: Use standard Compose Multiplatform with `androidx.compose.*` imports
- When working in Kuikly modules, prioritize Kuikly Compose DSL syntax over native Kuikly DSL when both options are available
- Use standard Compose patterns like `@Composable` functions, `setContent {}`, and familiar modifier patterns

**📖 Reference Documentation**: For detailed Kuikly UI API documentation, examples, and advanced features, refer to the comprehensive documentation in `docs/kuikly-ui/` directory.

#### Kuikly Compose DSL Pattern (For kuikly-shared/ and kuikly-android/ modules only)
```kotlin
// Example from kuikly-shared/src/commonMain/kotlin/io/ssttkkl/mahjongutils/app/kuikly/RouterPage.kt
package io.ssttkkl.mahjongutils.app.kuikly

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.coil3.rememberAsyncImagePainter
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Button
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.material3.TextField
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalActivity
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.pager.Pager

@Page("router", supportInLocal = true)
internal class ComposeRoutePager : ComposeContainer() {
  override fun willInit() {
    super.willInit()
    setContent {
      ComposeRouteImpl()
    }
  }
}

@Composable
internal fun ComposeRouteImpl() {
  var textFieldValue by remember { mutableStateOf("") }
  val localPager = LocalActivity.current.getPager() as Pager
  val statusBarHeight = LocalActivity.current.pageData.statusBarHeight

  Column(
    modifier = Modifier
      .padding(top = (statusBarHeight + 15).dp)
      .fillMaxSize()
      .background(Color.White),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "Kuikly页面路由",
      fontSize = 18.sp,
      color = Color(0xFF7B7FE4)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Image(
      modifier = Modifier.size(250.dp),
      painter = rememberAsyncImagePainter("https://example.com/logo.png"),
      contentDescription = null,
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        Modifier
          .weight(1f)
          .padding(horizontal = 12.dp)
          .background(
            Brush.horizontalGradient(
              colors = listOf(Color(0xFF7B7FE4), Color(0xFFA65CF9))
            ),
            shape = RoundedCornerShape(5.dp)
          )
          .padding(2.dp)
      ) {
        Box(
          modifier = Modifier
            .background(
              color = Color.White,
              shape = RoundedCornerShape(5.dp)
            )
            .padding(12.dp)
        ) {
          TextField(
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("输入pageName") },
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
          )
        }
      }

      Button(
        modifier = Modifier
          .padding(12.dp)
          .background(
            brush = Brush.horizontalGradient(
              colors = listOf(Color(0xFF7B7FE4), Color(0xFFA65CF9))
            ),
            shape = RoundedCornerShape(5.dp)
          )
          .padding(12.dp),
        onClick = {
          // Handle button click
        },
      ) {
        Text("跳转", color = Color.White)
      }
    }
  }
}
```

**Key features demonstrated:**
- **State management**: Using `remember` and `mutableStateOf` for reactive UI
- **Complex layouts**: Nested `Column`, `Row`, `Box` with proper modifiers
- **Styling**: Background gradients, rounded corners, padding, sizing
- **Images**: AsyncImagePainter for loading remote images
- **Text input**: TextField with custom styling and placeholders
- **Platform integration**: LocalActivity access for Kuikly-specific features
- **Page annotation**: `@Page` decorator for Kuikly routing system

#### Native Kuikly DSL Pattern (When Compose DSL insufficient in kuikly modules)
```kotlin
@Page("examplePage") 
internal class ExamplePage : BasePager() {
    override fun body(): ViewBuilder {
        return {
            View {
                attr {
                    flexDirection(FlexDirection.COLUMN)
                }
                Text {
                    attr {
                        text("Hello World")
                    }
                }
                Button {
                    attr {
                        // Kuikly-specific button attributes
                    }
                    event {
                        click { /* action */ }
                    }
                }
            }
        }
    }
}
```

### Kuikly Compose DSL Supported Components (kuikly-shared/ and kuikly-android/ modules only)
**IMPORTANT**: Only use components from the list below when working in kuikly modules. Kuikly Compose DSL currently supports ~90% of standard Compose APIs.

#### Package Structure
- Layout components: `com.tencent.kuikly.compose.foundation.layout.*`
- Material components: `com.tencent.kuikly.compose.material3.*`
- Foundation components: `com.tencent.kuikly.compose.foundation.*`

#### Supported Components List
**Layout Components:**
- `Box` - Stacked container with alignment
- `Column` - Vertical layout container
- `Row` - Horizontal layout container
- `FlowRow` - Auto-wrapping horizontal flow layout
- `FlowColumn` - Auto-wrapping vertical flow layout
- `Spacer` - Empty space placeholder

**List Components:**
- `LazyColumn` - Vertical scrolling list
- `LazyRow` - Horizontal scrolling list
- `LazyVerticalGrid` - Vertical grid layout
- `LazyHorizontalGrid` - Horizontal grid layout
- `LazyVerticalStaggeredGrid` - Vertical staggered grid
- `LazyHorizontalStaggeredGrid` - Horizontal staggered grid

**Pager Components:**
- `HorizontalPager` - Horizontal page switcher
- `VerticalPager` - Vertical page switcher

**Text & Input:**
- `Text` - Text display with rich formatting
- `BasicTextField` - Basic text input
- `TextField` - Material Design text field

**Interactive Components:**
- `Button` - Clickable button
- `Tab` - Individual tab item
- `TabRow` - Fixed tab row
- `ScrollableTabRow` - Scrollable tab row

**Container Components:**
- `Surface` - Material surface container
- `Card` - Material card container
- `Dialog` - Modal dialog

**Visual Components:**
- `Image` - Image display
- `Divider` - Visual separator

**Advanced:**
- `Layout` - Custom layout container with MeasurePolicy

#### Complete State Management, Animation & Gesture Support
- All official Compose state management APIs (`remember`, `mutableStateOf`, etc.)
- Complete animation system (all animation APIs and modifiers)
- Full gesture system (all gesture detection and modifiers)

#### Usage Notes
- Import components from correct Kuikly packages: `com.tencent.kuikly.compose.*`
- Do NOT use standard `androidx.compose.*` imports - they won't work
- All component APIs match standard Compose signatures
- Material 3 components are available but Material 2 may not be fully supported