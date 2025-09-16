# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Building and Running
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build to connected device/emulator
./gradlew installDebug

# Run tests
./gradlew test

# Run instrumented tests (requires connected device)
./gradlew connectedAndroidTest

# Clean build artifacts
./gradlew clean
```

### Code Quality
```bash
# Run Kotlin linter (ktlint via gradle plugin if added)
./gradlew ktlintCheck

# Format Kotlin code
./gradlint ktlintFormat
```

Note: This project does not currently have linting configured. Consider adding ktlint or detekt for code quality.

## Architecture Overview

This Android app provides dual interfaces (Terminal + GUI) for communicating with Chameleon Ultra RFID devices via USB/Bluetooth.

### Core Architecture Layers

1. **Communication Layer** (`communication/`): Handles device connections
   - `DeviceConnection` interface defines communication contract
   - `UsbConnection` and `BluetoothConnection` implement specific transports
   - `ConnectionManager` orchestrates connections and manages state via StateFlow

2. **Protocol Layer** (`device/`): Chameleon Ultra device communication
   - `ChameleonUltraProtocol` defines device command constants and data structures
   - `ChameleonUltraDevice` provides high-level device operations with coroutine support

3. **Command Translation** (`commands/`): Proxmark3 compatibility
   - `ProxmarkCommandParser` parses text commands into structured format
   - `CommandTranslator` converts Proxmark3 commands to Chameleon Ultra protocol
   - Supports command history and auto-completion suggestions

4. **UI Layer** (`ui/`): Dual interface implementation
   - **Terminal Interface**: `TerminalScreen` + `TerminalViewModel` for command-line experience
   - **GUI Interface**: `GuiScreen` with Compose UI for visual operations
   - `MainScreen` coordinates interface switching via `InterfaceSwitcher`

### Key Data Flow

```
User Input → Command Parser → Command Translator → Device Protocol → Connection Layer → Hardware
```

Response data flows back through the same layers using Kotlin StateFlow for reactive updates.

### Critical Integration Points

- **Connection State Management**: `ConnectionManager.ConnectionState` is observed throughout the app
- **Command Execution**: Terminal and GUI both use `CommandTranslator` for device operations
- **Data Reception**: `ConnectionManager.receivedData` StateFlow distributes device responses
- **Error Handling**: Connection failures propagate as `ConnectionState.Error` with messages

### Android-Specific Considerations

- **Permissions**: USB and Bluetooth permissions defined in AndroidManifest.xml
- **USB Device Filtering**: `device_filter.xml` specifies supported vendor/product IDs
- **Compose UI**: Modern declarative UI with Material Design 3
- **Coroutines**: All device I/O operations are suspending functions
- **ViewModel Pattern**: Terminal state managed via `TerminalViewModel` with proper lifecycle handling

### Testing Strategy

- Unit tests should focus on `CommandTranslator` and `ProxmarkCommandParser`
- Integration tests should mock `DeviceConnection` interface
- UI tests can use Compose testing framework for both Terminal and GUI screens
- Device protocol tests should use fake `ByteArray` responses

### Security Notes

This app is designed for legitimate security research with Chameleon Ultra devices. All RFID operations require explicit user action and device connection.