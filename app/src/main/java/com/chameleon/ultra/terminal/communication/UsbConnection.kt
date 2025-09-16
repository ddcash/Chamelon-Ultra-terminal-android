package com.chameleon.ultra.terminal.communication

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class UsbConnection(
    private val context: Context,
    private val device: UsbDevice? = null
) : DeviceConnection {

    private var serialPort: UsbSerialPort? = null
    private var driver: UsbSerialDriver? = null
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
            if (availableDrivers.isEmpty()) {
                return@withContext false
            }

            driver = if (device != null) {
                availableDrivers.find { it.device == device }
            } else {
                availableDrivers.firstOrNull()
            }

            driver?.let { drv ->
                val connection = usbManager.openDevice(drv.device)
                if (connection != null) {
                    serialPort = drv.ports[0].apply {
                        open(connection)
                        setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                    }
                    return@withContext true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            serialPort?.close()
            serialPort = null
            driver = null
        } catch (e: Exception) {
            // Handle disconnect errors
        }
    }

    override suspend fun sendData(data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        try {
            serialPort?.write(data, 1000) ?: return@withContext false
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun receiveData(): Flow<ByteArray> = flow {
        val buffer = ByteArray(1024)
        while (isConnected()) {
            try {
                val bytesRead = serialPort?.read(buffer, 1000) ?: 0
                if (bytesRead > 0) {
                    emit(buffer.copyOf(bytesRead))
                }
            } catch (e: Exception) {
                break
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun isConnected(): Boolean {
        return serialPort != null
    }

    override fun getConnectionType(): ConnectionType {
        return ConnectionType.USB
    }
}