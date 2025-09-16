package com.chameleon.ultra.terminal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chameleon.ultra.terminal.communication.ConnectionManager
import com.chameleon.ultra.terminal.device.ChameleonUltraDevice
import com.chameleon.ultra.terminal.terminal.TerminalViewModel
import com.chameleon.ultra.terminal.ui.components.ConnectionBar
import com.chameleon.ultra.terminal.ui.components.InterfaceSwitcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val connectionManager = remember { ConnectionManager(context) }
    val chameleonDevice = remember { ChameleonUltraDevice(connectionManager) }
    val terminalViewModel: TerminalViewModel = viewModel {
        TerminalViewModel(connectionManager, chameleonDevice)
    }

    var currentInterface by remember { mutableStateOf(InterfaceType.TERMINAL) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Connection status bar
        ConnectionBar(
            connectionManager = connectionManager,
            modifier = Modifier.fillMaxWidth()
        )

        // Interface switcher
        InterfaceSwitcher(
            currentInterface = currentInterface,
            onInterfaceChanged = { currentInterface = it },
            modifier = Modifier.fillMaxWidth()
        )

        // Main content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (currentInterface) {
                InterfaceType.TERMINAL -> {
                    TerminalScreen(
                        viewModel = terminalViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                InterfaceType.GUI -> {
                    GuiScreen(
                        connectionManager = connectionManager,
                        chameleonDevice = chameleonDevice,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

enum class InterfaceType {
    TERMINAL,
    GUI
}