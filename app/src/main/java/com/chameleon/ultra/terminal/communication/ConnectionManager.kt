package com.chameleon.ultra.terminal.communication

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConnectionManager(private val context: Context) {

    private var currentConnection: DeviceConnection? = null
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _receivedData = MutableStateFlow<ByteArray?>(null)
    val receivedData: StateFlow<ByteArray?> = _receivedData.asStateFlow()

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connecting : ConnectionState()
        object Connected : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    companion object {
        private const val TAG = "ConnectionManager"
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        }
    }

    suspend fun connectUsb(device: UsbDevice? = null): Boolean {
        try {
            Log.d(TAG, "Attempting USB connection")
            _connectionState.value = ConnectionState.Connecting
            currentConnection = UsbConnection(context, device)

            val success = currentConnection?.connect() ?: false
            _connectionState.value = if (success) {
                Log.d(TAG, "USB connection successful")
                startDataReceiving()
                ConnectionState.Connected
            } else {
                Log.e(TAG, "USB connection failed")
                ConnectionState.Error("Failed to connect via USB")
            }

            return success
        } catch (e: Exception) {
            Log.e(TAG, "USB connection error", e)
            _connectionState.value = ConnectionState.Error("USB connection error: ${e.message}")
            return false
        }
    }

    suspend fun connectBluetooth(device: BluetoothDevice? = null): Boolean {
        try {
            Log.d(TAG, "Attempting Bluetooth connection")
            
            if (!hasBluetoothPermissions()) {
                Log.e(TAG, "Missing Bluetooth permissions")
                _connectionState.value = ConnectionState.Error("Missing Bluetooth permissions")
                return false
            }

            _connectionState.value = ConnectionState.Connecting
            currentConnection = BluetoothConnection(context, device)

            val success = currentConnection?.connect() ?: false
            _connectionState.value = if (success) {
                Log.d(TAG, "Bluetooth connection successful")
                startDataReceiving()
                ConnectionState.Connected
            } else {
                Log.e(TAG, "Bluetooth connection failed")
                ConnectionState.Error("Failed to connect via Bluetooth")
            }

            return success
        } catch (e: Exception) {
            Log.e(TAG, "Bluetooth connection error", e)
            _connectionState.value = ConnectionState.Error("Bluetooth connection error: ${e.message}")
            return false
        }
    }

    suspend fun disconnect() {
        try {
            Log.d(TAG, "Disconnecting device")
            currentConnection?.disconnect()
            currentConnection = null
            _connectionState.value = ConnectionState.Disconnected
            Log.d(TAG, "Device disconnected successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during disconnect", e)
        }
    }

    suspend fun sendCommand(command: String): Boolean {
        return currentConnection?.sendData(command.toByteArray()) ?: false
    }

    suspend fun sendData(data: ByteArray): Boolean {
        return currentConnection?.sendData(data) ?: false
    }

    fun isConnected(): Boolean {
        return currentConnection?.isConnected() == true
    }

    fun getConnectionType(): ConnectionType {
        return currentConnection?.getConnectionType() ?: ConnectionType.NONE
    }

    private fun startDataReceiving() {
        currentConnection?.receiveData()?.let { flow ->
            CoroutineScope(Dispatchers.IO).launch {
                flow.collect { data ->
                    _receivedData.value = data
                }
            }
        }
    }
}