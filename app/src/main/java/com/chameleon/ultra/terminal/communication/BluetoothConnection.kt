package com.chameleon.ultra.terminal.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
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
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            val targetDevice = device ?: bluetoothAdapter?.bondedDevices?.find {
                it.name?.contains("Chameleon", ignoreCase = true) == true
            }

            targetDevice?.let { btDevice ->
                bluetoothSocket = btDevice.createRfcommSocketToServiceRecord(SPP_UUID)
                bluetoothAdapter?.cancelDiscovery()

                bluetoothSocket?.connect()
                inputStream = bluetoothSocket?.inputStream
                outputStream = bluetoothSocket?.outputStream

                return@withContext true
            }
            false
        } catch (e: IOException) {
            false
        }
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
            inputStream = null
            outputStream = null
            bluetoothSocket = null
        } catch (e: IOException) {
            // Handle disconnect errors
        }
    }

    override suspend fun sendData(data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        try {
            outputStream?.write(data)
            outputStream?.flush()
            true
        } catch (e: IOException) {
            false
        }
    }

    override fun receiveData(): Flow<ByteArray> = flow {
        val buffer = ByteArray(1024)
        while (isConnected()) {
            try {
                val bytesRead = inputStream?.read(buffer) ?: 0
                if (bytesRead > 0) {
                    emit(buffer.copyOf(bytesRead))
                }
            } catch (e: IOException) {
                break
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun isConnected(): Boolean {
        return bluetoothSocket?.isConnected == true
    }

    override fun getConnectionType(): ConnectionType {
        return ConnectionType.BLUETOOTH
    }
}