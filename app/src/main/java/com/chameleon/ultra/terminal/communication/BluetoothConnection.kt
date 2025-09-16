package com.chameleon.ultra.terminal.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

@SuppressLint("MissingPermission")
class BluetoothConnection(
    private val context: Context,
    private val device: BluetoothDevice? = null
) : DeviceConnection {

    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    companion object {
        private const val TAG = "BluetoothConnection"
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting Bluetooth connection")

            if (bluetoothAdapter == null) {
                Log.e(TAG, "Bluetooth adapter not available")
                return@withContext false
            }

            val targetDevice = device ?: bluetoothAdapter.bondedDevices?.find {
                it.name?.contains("Chameleon", ignoreCase = true) == true
            }

            if (targetDevice == null) {
                Log.e(TAG, "No suitable Chameleon device found. Paired devices: ${bluetoothAdapter.bondedDevices?.map { it.name }}")
                return@withContext false
            }

            Log.d(TAG, "Attempting to connect to device: ${targetDevice.name} (${targetDevice.address})")

            targetDevice.let { btDevice ->
                bluetoothSocket = btDevice.createRfcommSocketToServiceRecord(SPP_UUID)
                Log.d(TAG, "Created RFCOMM socket with SPP UUID")

                bluetoothAdapter.cancelDiscovery()
                Log.d(TAG, "Cancelled Bluetooth discovery")

                bluetoothSocket?.connect()
                Log.d(TAG, "Bluetooth socket connected successfully")

                inputStream = bluetoothSocket?.inputStream
                outputStream = bluetoothSocket?.outputStream
                Log.d(TAG, "Input/Output streams initialized")

                return@withContext true
            }
        } catch (e: IOException) {
            Log.e(TAG, "Bluetooth connection failed", e)
            // Clean up on failure
            try {
                bluetoothSocket?.close()
                bluetoothSocket = null
                inputStream = null
                outputStream = null
            } catch (cleanupException: IOException) {
                Log.e(TAG, "Error during cleanup after failed connection", cleanupException)
            }
            return@withContext false
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Bluetooth connection", e)
            return@withContext false
        }
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Disconnecting Bluetooth device")
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
            inputStream = null
            outputStream = null
            bluetoothSocket = null
            Log.d(TAG, "Bluetooth device disconnected successfully")
        } catch (e: IOException) {
            Log.e(TAG, "Error during Bluetooth disconnect", e)
        }
    }

    override suspend fun sendData(data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        try {
            val stream = outputStream ?: return@withContext false
            stream.write(data)
            stream.flush()
            Log.d(TAG, "Sent ${data.size} bytes via Bluetooth")
            true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to send data via Bluetooth", e)
            false
        }
    }

    override fun receiveData(): Flow<ByteArray> = flow {
        val buffer = ByteArray(1024)
        Log.d(TAG, "Starting Bluetooth data reception")
        while (isConnected()) {
            try {
                val bytesRead = inputStream?.read(buffer) ?: 0
                if (bytesRead > 0) {
                    Log.d(TAG, "Received $bytesRead bytes via Bluetooth")
                    emit(buffer.copyOf(bytesRead))
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error reading Bluetooth data", e)
                break
            }
        }
        Log.d(TAG, "Bluetooth data reception stopped")
    }.flowOn(Dispatchers.IO)

    override fun isConnected(): Boolean {
        return bluetoothSocket?.isConnected == true
    }

    override fun getConnectionType(): ConnectionType {
        return ConnectionType.BLUETOOTH
    }
}