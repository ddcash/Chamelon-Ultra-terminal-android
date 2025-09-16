package com.chameleon.ultra.terminal.commands

data class ProxmarkCommand(
    val command: String,
    val args: List<String> = emptyList()
) {
    fun getFullCommand(): String = if (args.isEmpty()) command else "$command ${args.joinToString(" ")}"
}

enum class CommandCategory {
    HF,
    LF,
    DATA,
    HW,
    SCRIPT,
    TRACE,
    EMV,
    OTHER
}