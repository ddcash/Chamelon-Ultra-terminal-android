package com.chameleon.ultra.terminal.commands

import com.chameleon.ultra.terminal.device.ChameleonUltraProtocol

class CommandTranslator {

    suspend fun translateProxmarkToChameleon(proxmarkCommand: ProxmarkCommand): ByteArray? {
        return when (proxmarkCommand.command) {
            "hw" -> when (proxmarkCommand.args.firstOrNull()) {
                "version" -> ChameleonUltraProtocol.createGetVersionCommand().toByteArray()
                "status" -> ChameleonUltraProtocol.createGetVersionCommand().toByteArray()
                else -> null
            }

            "hf" -> when (proxmarkCommand.args.getOrNull(0)) {
                "14a" -> when (proxmarkCommand.args.getOrNull(1)) {
                    "info", "reader" -> ChameleonUltraProtocol.createHf14aScanCommand().toByteArray()
                    "raw" -> {
                        val data = parseHexData(proxmarkCommand.args.drop(2))
                        if (data != null) {
                            ChameleonUltraProtocol.createHf14aRawCommand(data).toByteArray()
                        } else null
                    }
                    else -> null
                }
                "search" -> ChameleonUltraProtocol.createHf14aScanCommand().toByteArray()
                else -> null
            }

            "data" -> when (proxmarkCommand.args.firstOrNull()) {
                "setslot" -> {
                    val slot = proxmarkCommand.args.getOrNull(1)?.toIntOrNull()
                    if (slot != null && slot in 0..7) {
                        ChameleonUltraProtocol.createSetSlotCommand(slot.toByte()).toByteArray()
                    } else null
                }
                else -> null
            }

            else -> null
        }
    }

    private fun parseHexData(args: List<String>): ByteArray? {
        if (args.isEmpty()) return null

        return try {
            val hexString = args.joinToString("").replace(" ", "")
            hexString.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        } catch (e: Exception) {
            null
        }
    }

    fun getCommandHelp(command: String): String {
        return when (command.lowercase()) {
            "hw version" -> "Get device firmware version"
            "hw status" -> "Get device status information"
            "hf search" -> "Search for HF tags"
            "hf 14a info" -> "Get ISO14443A tag information"
            "hf 14a reader" -> "Read ISO14443A tag"
            "hf 14a raw" -> "Send raw ISO14443A command"
            "data setslot" -> "Set active emulation slot (0-7)"
            "help" -> "Show available commands"
            "quit", "exit" -> "Exit the application"
            "clear", "cls" -> "Clear terminal output"
            else -> "Unknown command. Type 'help' for available commands."
        }
    }

    fun getAvailableCommands(): List<String> {
        return listOf(
            "hw version - Get device firmware version",
            "hw status - Get device status",
            "hf search - Search for HF tags",
            "hf 14a info - Get ISO14443A tag info",
            "hf 14a reader - Read ISO14443A tag",
            "hf 14a raw <hex> - Send raw ISO14443A command",
            "data setslot <0-7> - Set active emulation slot",
            "help - Show this help",
            "clear - Clear terminal",
            "quit - Exit application"
        )
    }
}