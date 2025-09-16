# Chameleon Ultra Terminal Android

![Build Status](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/workflows/Build%20Android%20APK/badge.svg)
![Release](https://img.shields.io/github/v/release/YOUR_USERNAME/Chamelon-Ultra-terminal-android)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

<!-- Build trigger: Retesting after connection fixes -->

An Android application that provides full terminal and GUI interface for the Chameleon Ultra device, implementing Proxmark3-compatible commands for RFID/NFC operations.

## 📱 Download APK

You can download the latest APK from the [Releases](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/releases) page.

## ✨ Features

### 🔌 Dual Connectivity
- **USB Connection**: High-speed, reliable wired connection
- **Bluetooth Connection**: Wireless convenience for mobile operations

### 💻 Dual Interface Modes
- **Terminal Mode**: Full command-line interface with Proxmark3 compatibility
- **GUI Mode**: User-friendly graphical interface for common operations

### 📡 Comprehensive RFID/NFC Support
- **Low Frequency (LF)**: 125 kHz tags (EM4x, HID, etc.)
- **High Frequency (HF)**: 13.56 MHz tags (MIFARE, NTAG, etc.)
- **Card Emulation**: Clone and emulate various card types
- **Reader Mode**: Detect and analyze unknown cards

### 🎛️ Device Control
- Real-time device status monitoring
- Battery level indication
- LED control and feedback
- Button simulation

## 🚀 Installation

### Prerequisites
- Android 7.0 (API level 24) or higher
- Chameleon Ultra device
- USB OTG cable (for USB connection) or Bluetooth pairing

### Install Steps
1. Download the latest APK from [Releases](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/releases)
2. Enable "Install from unknown sources" in Android settings
3. Install the APK
4. Grant required permissions:
   - USB device access
   - Bluetooth (for wireless connection)
   - Storage (for saving dumps and logs)

## 📖 Usage

### Initial Setup
1. **USB Connection**: Connect Chameleon Ultra via USB OTG cable
2. **Bluetooth Connection**: Pair device in Android settings first
3. Launch the app and select connection type
4. Wait for connection confirmation

### Terminal Mode
Access full Proxmark3-compatible command set:

```bash
# Device information
hw version
hw status

# LF operations
lf search
lf em 410x read
lf em 410x clone --id 0102030405

# HF operations
hf search
hf mf rdbl --blk 0 --key FFFFFFFFFFFF
hf mf wrbl --blk 4 --key FFFFFFFFFFFF -d 01020304050607080910111213141516
```

### GUI Mode
- **Quick Actions**: Common operations with visual feedback
- **Card Manager**: Save, organize, and manage card dumps
- **Device Status**: Real-time monitoring and control
- **Settings**: Connection preferences and app configuration

## 🛠️ Development

### Building from Source

1. **Clone Repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android.git
   cd Chamelon-Ultra-terminal-android
   ```

2. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install to Device**:
   ```bash
   ./gradlew installDebug
   ```

### Architecture

- **Connection Layer**: Abstracted USB/Bluetooth communication
- **Protocol Layer**: Chameleon Ultra command processing
- **Command Translation**: Proxmark3 to Chameleon Ultra command mapping
- **UI Layer**: Dual-mode terminal and GUI interfaces

### Dependencies
- **USB Serial**: [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)
- **Bluetooth**: Android Bluetooth API
- **UI**: Jetpack Compose + Material Design 3
- **Async**: Kotlin Coroutines + Flow

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ⚠️ Disclaimer

This application is for educational and legitimate security research purposes only. Users are responsible for compliance with local laws and regulations regarding RFID/NFC devices and operations.

## 🙏 Acknowledgments

- [Proxmark3](https://github.com/RfidResearchGroup/proxmark3) project for command compatibility
- [Chameleon Ultra](https://github.com/RfidResearchGroup/ChameleonUltra) firmware team
- [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android) library

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/issues)
- **Discussions**: [GitHub Discussions](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/discussions)
- **Documentation**: [Wiki](https://github.com/YOUR_USERNAME/Chamelon-Ultra-terminal-android/wiki)