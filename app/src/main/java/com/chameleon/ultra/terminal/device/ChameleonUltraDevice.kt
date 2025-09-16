package com.chameleon.ultra.terminal.device

import com.chameleon.ultra.terminal.communication.ConnectionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

class ChameleonUltraDevice(
    private val connectionManager: ConnectionManager
) {

    suspend fun getVersion(): String? {
        val command = ChameleonUltraProtocol.createGetVersionCommand()
        if (!connectionManager.sendData(command.toByteArray())) {
            return null
        }

        return withTimeout(5000) {
            var response: ByteArray? = null
            while (response == null) {
                response = connectionManager.receivedData.value
                delay(100)
            }

            val parsedResponse = ChameleonUltraProtocol.parseResponse(response)
            if (parsedResponse?.status == ChameleonUltraProtocol.STATUS_OK.toByte()) {
                String(parsedResponse.data)
            } else {
                null
            }
        }
    }

    suspend fun scanHf14a(): List<String>? {
        val command = ChameleonUltraProtocol.createHf14aScanCommand()
        if (!connectionManager.sendData(command.toByteArray())) {
            return null
        }

        return withTimeout(10000) {
            var response: ByteArray? = null
            while (response == null) {
                response = connectionManager.receivedData.value
                delay(100)
            }

            val parsedResponse = ChameleonUltraProtocol.parseResponse(response)
            if (parsedResponse?.status == ChameleonUltraProtocol.STATUS_OK.toByte()) {
                // Parse scan results (simplified implementation)
                val results = mutableListOf<String>()
                val data = parsedResponse.data
                if (data.isNotEmpty()) {
                    results.add("Card found: ${data.joinToString("") { "%02x".format(it) }}")
                }
                results
            } else {
                null
            }
        }
    }

    suspend fun setSlot(slot: Int): Boolean {
        val command = ChameleonUltraProtocol.createSetSlotCommand(slot.toByte())
        if (!connectionManager.sendData(command.toByteArray())) {
            return false
        }

        return withTimeout(3000) {
            var response: ByteArray? = null
            while (response == null) {
                response = connectionManager.receivedData.value
                delay(50)
            }

            val parsedResponse = ChameleonUltraProtocol.parseResponse(response)
            parsedResponse?.status == ChameleonUltraProtocol.STATUS_OK.toByte()
        }
    }

    suspend fun setUid(uid: String): Boolean {
        val uidBytes = uid.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val command = ChameleonUltraProtocol.createSetUidCommand(uidBytes)

        if (!connectionManager.sendData(command.toByteArray())) {
            return false
        }

        return withTimeout(3000) {
            var response: ByteArray? = null
            while (response == null) {
                response = connectionManager.receivedData.value
                delay(50)
            }

            val parsedResponse = ChameleonUltraProtocol.parseResponse(response)
            parsedResponse?.status == ChameleonUltraProtocol.STATUS_OK.toByte()
        }
    }

    suspend fun sendRawCommand(data: ByteArray): ByteArray? {
        if (!connectionManager.sendData(data)) {
            return null
        }

        return withTimeout(5000) {
            var response: ByteArray? = null
            while (response == null) {
                response = connectionManager.receivedData.value
                delay(100)
            }
            response
        }
    }
}