package com.chameleon.ultra.terminal.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chameleon.ultra.terminal.commands.CommandTranslator
import com.chameleon.ultra.terminal.commands.ProxmarkCommandParser
import com.chameleon.ultra.terminal.communication.ConnectionManager
import com.chameleon.ultra.terminal.device.ChameleonUltraDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TerminalViewModel(
    private val connectionManager: ConnectionManager,
    private val chameleonDevice: ChameleonUltraDevice
) : ViewModel() {

    private val commandParser = ProxmarkCommandParser()
    private val commandTranslator = CommandTranslator()

    private val _terminalOutput = MutableStateFlow<List<TerminalLine>>(
        listOf(
            TerminalLine("Chameleon Ultra Terminal", TerminalLineType.INFO),
            TerminalLine("Type 'help' for available commands", TerminalLineType.INFO),
            TerminalLine("", TerminalLineType.OUTPUT)
        )
    )
    val terminalOutput: StateFlow<List<TerminalLine>> = _terminalOutput.asStateFlow()

    private val _currentInput = MutableStateFlow("")
    val currentInput: StateFlow<String> = _currentInput.asStateFlow()

    private val _commandHistory = MutableStateFlow<List<String>>(emptyList())
    private var historyIndex = -1

    init {
        // Listen for device responses
        viewModelScope.launch {
            connectionManager.receivedData.collect { data ->
                data?.let {
                    val response = String(it, Charsets.UTF_8)
                    addOutputLine(response, TerminalLineType.RESPONSE)
                }
            }
        }
    }

    fun updateInput(input: String) {
        _currentInput.value = input
    }

    fun executeCommand(command: String) {
        if (command.isBlank()) return

        val trimmedCommand = command.trim()
        addOutputLine("proxmark3> $trimmedCommand", TerminalLineType.COMMAND)

        // Add to history
        val history = _commandHistory.value.toMutableList()
        history.add(trimmedCommand)
        if (history.size > 100) history.removeFirst()
        _commandHistory.value = history
        historyIndex = -1

        // Clear input
        _currentInput.value = ""

        // Handle built-in commands
        when (trimmedCommand.lowercase()) {
            "help" -> {
                commandTranslator.getAvailableCommands().forEach { helpLine ->
                    addOutputLine(helpLine, TerminalLineType.INFO)
                }
                return
            }
            "clear", "cls" -> {
                clearTerminal()
                return
            }
            "quit", "exit" -> {
                // Handle app exit
                addOutputLine("Goodbye!", TerminalLineType.INFO)
                return
            }
        }

        // Parse and execute Proxmark command
        val parsedCommand = commandParser.parseCommand(trimmedCommand)
        if (parsedCommand == null) {
            addOutputLine("Invalid command format", TerminalLineType.ERROR)
            return
        }

        if (!commandParser.isValidCommand(parsedCommand.command)) {
            addOutputLine("Unknown command: ${parsedCommand.command}", TerminalLineType.ERROR)
            val suggestions = commandParser.getCommandSuggestions(parsedCommand.command)
            if (suggestions.isNotEmpty()) {
                addOutputLine("Did you mean: ${suggestions.joinToString(", ")}", TerminalLineType.INFO)
            }
            return
        }

        // Execute the command
        viewModelScope.launch {
            try {
                if (!connectionManager.isConnected()) {
                    addOutputLine("Device not connected", TerminalLineType.ERROR)
                    return@launch
                }

                val chameleonCommand = commandTranslator.translateProxmarkToChameleon(parsedCommand)
                if (chameleonCommand == null) {
                    addOutputLine("Command not supported: ${parsedCommand.getFullCommand()}", TerminalLineType.ERROR)
                    return@launch
                }

                addOutputLine("Executing: ${parsedCommand.getFullCommand()}", TerminalLineType.INFO)

                val success = connectionManager.sendData(chameleonCommand)
                if (!success) {
                    addOutputLine("Failed to send command", TerminalLineType.ERROR)
                }

            } catch (e: Exception) {
                addOutputLine("Error executing command: ${e.message}", TerminalLineType.ERROR)
            }
        }
    }

    fun getPreviousCommand(): String? {
        val history = _commandHistory.value
        if (history.isEmpty()) return null

        if (historyIndex == -1) {
            historyIndex = history.size - 1
        } else if (historyIndex > 0) {
            historyIndex--
        }

        return if (historyIndex >= 0) history[historyIndex] else null
    }

    fun getNextCommand(): String? {
        val history = _commandHistory.value
        if (history.isEmpty() || historyIndex == -1) return null

        historyIndex++
        return if (historyIndex < history.size) {
            history[historyIndex]
        } else {
            historyIndex = -1
            ""
        }
    }

    private fun addOutputLine(text: String, type: TerminalLineType) {
        val currentOutput = _terminalOutput.value.toMutableList()
        currentOutput.add(TerminalLine(text, type))

        // Limit output to 1000 lines
        if (currentOutput.size > 1000) {
            currentOutput.removeFirst()
        }

        _terminalOutput.value = currentOutput
    }

    private fun clearTerminal() {
        _terminalOutput.value = listOf(
            TerminalLine("Terminal cleared", TerminalLineType.INFO),
            TerminalLine("", TerminalLineType.OUTPUT)
        )
    }
}

data class TerminalLine(
    val text: String,
    val type: TerminalLineType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TerminalLineType {
    COMMAND,
    RESPONSE,
    ERROR,
    INFO,
    OUTPUT
}