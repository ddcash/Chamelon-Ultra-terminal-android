# Chameleon Ultra Terminal Android

![Build Status](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/workflows/Build%20Android%20APK/badge.svg)
![Release](https://img.shields.io/github/v/release/YOUR_USERNAME/Chamelon-Ultra-terminal-android)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

An Android application that provides full terminal and GUI interface for the Chameleon Ultra device, implementing Proxmark3-compatible commands for RFID/NFC operations.

## 📱 Download APK

**🚀 [Download Latest APK from Releases](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/releases/latest)**

*APKs are automatically built and released when code is pushed to the main branch.*

## ✨ Features

### 🔌 Connection Support
- **USB Serial**: Direct USB connection to Chameleon Ultra
- **Bluetooth**: Wireless connection via Bluetooth
- **Auto-detection**: Automatic device discovery and connection

### 💻 Dual Interface
- **Terminal Mode**: Classic command-line interface with Proxmark3 command compatibility
- **GUI Mode**: User-friendly graphical interface for common operations
- **Seamless switching**: Toggle between terminal and GUI modes instantly

### 📡 Proxmark3 Compatibility
Supports major Proxmark3 commands including:
- `hw version` - Get device firmware version
- `hw status` - Get device status
- `hf search` - Search for HF tags
- `hf 14a info` - ISO14443A tag information
- `hf 14a reader` - Read ISO14443A tags
- `hf 14a raw` - Send raw ISO14443A commands
- `data setslot` - Set active emulation slot

### 🎛️ GUI Features
- **Device Information**: View firmware version and status
- **Slot Management**: Switch between 8 emulation slots
- **UID Configuration**: Set custom UIDs for emulation
- **HF Scanning**: Scan for high-frequency RFID tags
- **Quick Actions**: Start/stop emulation with one tap

## 📋 Requirements

- **Android 7.0** (API level 24) or higher
- **USB Host support** for USB connections
- **Bluetooth** capability for wireless connections
- **Chameleon Ultra device**

## 🚀 Installation

### Option 1: Download Pre-built APK (Recommended)
1. Go to the [Releases page](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/releases)
2. Download the latest APK file
3. Enable "Install from unknown sources" in Android settings
4. Install the APK
5. Grant USB and Bluetooth permissions when prompted

### Option 2: Build from Source
1. Clone this repository
2. Open in Android Studio
3. Build and install on your Android device
4. Grant USB and Bluetooth permissions when prompted

## 📖 Usage

### Connecting to Device

#### USB Connection:
1. Connect Chameleon Ultra via USB cable
2. Tap "USB" button in the connection bar
3. Grant USB permission when prompted

#### Bluetooth Connection:
1. Pair Chameleon Ultra in Android Bluetooth settings
2. Tap "BT" button in the connection bar
3. Grant Bluetooth permissions when prompted

### Terminal Mode

The terminal provides a familiar Proxmark3 command-line experience:

```
proxmark3> hw version
Chameleon Ultra v2.0

proxmark3> hf search
ISO14443A tag found
UID: 04 AB 12 34

proxmark3> hf 14a info
Tag Type: MIFARE Classic 1K
```

### GUI Mode

The GUI interface offers:
- **Device status** at the top
- **Slot management** with visual slot selector
- **UID configuration** with hex input validation
- **HF scanning** with results display
- **Quick actions** for common operations

## 🏗️ Architecture

### Core Components

- **ConnectionManager**: Handles USB and Bluetooth communication
- **ChameleonUltraProtocol**: Device protocol implementation
- **CommandTranslator**: Proxmark3 to Chameleon Ultra command translation
- **TerminalViewModel**: Terminal interface logic
- **UI Components**: Compose-based user interface

### Communication Flow

```
User Input → Command Parser → Command Translator → Device Protocol → USB/Bluetooth → Chameleon Ultra
```

## 🔧 Development

### Dependencies

- **Kotlin** - Primary development language
- **Jetpack Compose** - Modern Android UI toolkit
- **USB Serial for Android** - USB communication library
- **Coroutines** - Asynchronous programming

### Build Configuration

```kotlin
android {
    compileSdk = 34
    minSdk = 24
    targetSdk = 34
}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## ⚖️ Security Considerations

This application is designed for **legitimate security research and testing purposes only**. Users are responsible for:

- Complying with local laws and regulations
- Obtaining proper authorization before testing RFID systems
- Using the application ethically and responsibly
- Not engaging in unauthorized access or malicious activities

## 📄 License

This project is open source under the MIT License. See the [LICENSE](LICENSE) file for details.

## ⚠️ Disclaimer

This software is provided for educational and research purposes only. The developers are not responsible for any misuse of this application. Users must ensure they have proper authorization before testing any RFID systems.

## 🛠️ Support

- 📋 [Issues](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/issues)
- 💬 [Discussions](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/discussions)
- 📖 [Wiki](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/wiki)

---

**Built with ❤️ for the RFID security research community**