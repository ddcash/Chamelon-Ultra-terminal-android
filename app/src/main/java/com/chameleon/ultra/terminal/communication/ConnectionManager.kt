package com.chameleon.ultra.terminal.communication

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.hardware.usb.UsbDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    suspend fun connectUsb(device: UsbDevice? = null): Boolean {
        _connectionState.value = ConnectionState.Connecting
        currentConnection = UsbConnection(context, device)

        val success = currentConnection?.connect() ?: false
        _connectionState.value = if (success) {
            startDataReceiving()
            ConnectionState.Connected
        } else {
            ConnectionState.Error("Failed to connect via USB")
        }

        return success
    }

    suspend fun connectBluetooth(device: BluetoothDevice? = null): Boolean {
        _connectionState.value = ConnectionState.Connecting
        currentConnection = BluetoothConnection(context, device)

        val success = currentConnection?.connect() ?: false
        _connectionState.value = if (success) {
            startDataReceiving()
            ConnectionState.Connected
        } else {
            ConnectionState.Error("Failed to connect via Bluetooth")
        }

        return success
    }

    suspend fun disconnect() {
        currentConnection?.disconnect()
        currentConnection = null
        _connectionState.value = ConnectionState.Disconnected
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
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                flow.collect { data ->
                    _receivedData.value = data
                }
            }
        }
    }
}

private fun kotlinx.coroutines.CoroutineScope.launch(block: suspend kotlinx.coroutines.CoroutineScope.() -> Unit): kotlinx.coroutines.Job {
    return kotlinx.coroutines.launch(block = block)
}