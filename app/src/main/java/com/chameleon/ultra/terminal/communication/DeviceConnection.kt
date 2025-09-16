package com.chameleon.ultra.terminal.communication

import kotlinx.coroutines.flow.Flow

interface DeviceConnection {
    suspend fun connect(): Boolean
    suspend fun disconnect()
    suspend fun sendData(data: ByteArray): Boolean
    fun receiveData(): Flow<ByteArray>
    fun isConnected(): Boolean
    fun getConnectionType(): ConnectionType
}