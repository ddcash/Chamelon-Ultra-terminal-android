package com.chameleon.ultra.terminal.device

object ChameleonUltraProtocol {

    // Chameleon Ultra command constants
    const val CMD_GET_VERSION = 0x1000
    const val CMD_SET_SLOT = 0x1001
    const val CMD_GET_SLOT = 0x1002
    const val CMD_HF14A_SCAN = 0x2000
    const val CMD_HF14A_RAW = 0x2001
    const val CMD_EMULATOR_START = 0x3000
    const val CMD_EMULATOR_STOP = 0x3001
    const val CMD_SET_UID = 0x3002
    const val CMD_SET_SAK = 0x3003
    const val CMD_SET_ATQA = 0x3004

    // Response status codes
    const val STATUS_OK = 0x00
    const val STATUS_ERROR = 0x01
    const val STATUS_INVALID_PARAM = 0x02
    const val STATUS_TIMEOUT = 0x03

    data class Command(
        val cmd: Int,
        val data: ByteArray = byteArrayOf()
    ) {
        fun toByteArray(): ByteArray {
            val cmdBytes = ByteArray(4)
            cmdBytes[0] = (cmd shr 24).toByte()
            cmdBytes[1] = (cmd shr 16).toByte()
            cmdBytes[2] = (cmd shr 8).toByte()
            cmdBytes[3] = cmd.toByte()

            return cmdBytes + data
        }
    }

    data class Response(
        val status: Byte,
        val data: ByteArray
    )

    fun parseResponse(rawData: ByteArray): Response? {
        if (rawData.isEmpty()) return null

        val status = rawData[0]
        val data = if (rawData.size > 1) {
            rawData.copyOfRange(1, rawData.size)
        } else {
            byteArrayOf()
        }

        return Response(status, data)
    }

    fun createGetVersionCommand(): Command {
        return Command(CMD_GET_VERSION)
    }

    fun createHf14aScanCommand(): Command {
        return Command(CMD_HF14A_SCAN)
    }

    fun createSetSlotCommand(slot: Byte): Command {
        return Command(CMD_SET_SLOT, byteArrayOf(slot))
    }

    fun createSetUidCommand(uid: ByteArray): Command {
        return Command(CMD_SET_UID, uid)
    }

    fun createHf14aRawCommand(data: ByteArray): Command {
        return Command(CMD_HF14A_RAW, data)
    }
}