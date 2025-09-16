package com.chameleon.ultra.terminal.commands

class ProxmarkCommandParser {

    fun parseCommand(input: String): ProxmarkCommand? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null

        val parts = trimmed.split("\\s+".toRegex())
        if (parts.isEmpty()) return null

        val command = parts[0].lowercase()
        val args = if (parts.size > 1) parts.drop(1) else emptyList()

        return ProxmarkCommand(command, args)
    }

    fun getCommandCategory(command: String): CommandCategory {
        return when {
            command.startsWith("hf") -> CommandCategory.HF
            command.startsWith("lf") -> CommandCategory.LF
            command.startsWith("data") -> CommandCategory.DATA
            command.startsWith("hw") -> CommandCategory.HW
            command.startsWith("script") -> CommandCategory.SCRIPT
            command.startsWith("trace") -> CommandCategory.TRACE
            command.startsWith("emv") -> CommandCategory.EMV
            else -> CommandCategory.OTHER
        }
    }

    fun isValidCommand(command: String): Boolean {
        return getKnownCommands().any { it.startsWith(command.lowercase()) }
    }

    fun getCommandSuggestions(partial: String): List<String> {
        val lower = partial.lowercase()
        return getKnownCommands().filter { it.startsWith(lower) }.take(10)
    }

    private fun getKnownCommands(): List<String> {
        return listOf(
            // HF commands
            "hf search",
            "hf 14a info",
            "hf 14a reader",
            "hf 14a raw",
            "hf 14a apdu",
            "hf 14a dump",
            "hf 14a clone",
            "hf 14b info",
            "hf 14b reader",
            "hf 14b raw",
            "hf 15 info",
            "hf 15 reader",
            "hf mf rdbl",
            "hf mf rdsc",
            "hf mf wrbl",
            "hf mf chk",
            "hf mf nested",
            "hf mf hardnested",
            "hf mf autopwn",
            "hf mf dump",
            "hf mf restore",
            "hf mf clone",
            "hf mfu info",
            "hf mfu dump",
            "hf mfu wrbl",

            // LF commands
            "lf search",
            "lf read",
            "lf tune",
            "lf em 410x reader",
            "lf em 410x clone",
            "lf em 410x dump",
            "lf hid reader",
            "lf hid clone",
            "lf indala reader",
            "lf indala clone",
            "lf t55xx detect",
            "lf t55xx read",
            "lf t55xx write",
            "lf t55xx dump",

            // Data commands
            "data hex2bin",
            "data bin2hex",
            "data bitsamples",
            "data samples",
            "data autocorr",
            "data dirthreshold",
            "data undecimate",

            // Hardware commands
            "hw status",
            "hw ping",
            "hw version",
            "hw tune",
            "hw setlfdivisor",
            "hw setmux",
            "hw reset",
            "hw standalone",

            // Other common commands
            "help",
            "quit",
            "exit",
            "clear",
            "cls"
        )
    }
}