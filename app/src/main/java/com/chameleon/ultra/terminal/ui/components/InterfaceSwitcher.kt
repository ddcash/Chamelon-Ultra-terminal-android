package com.chameleon.ultra.terminal.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chameleon.ultra.terminal.ui.InterfaceType

@Composable
fun InterfaceSwitcher(
    currentInterface: InterfaceType,
    onInterfaceChanged: (InterfaceType) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = currentInterface.ordinal,
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Tab(
            selected = currentInterface == InterfaceType.TERMINAL,
            onClick = { onInterfaceChanged(InterfaceType.TERMINAL) },
            text = { Text("Terminal") }
        )

        Tab(
            selected = currentInterface == InterfaceType.GUI,
            onClick = { onInterfaceChanged(InterfaceType.GUI) },
            text = { Text("GUI") }
        )
    }
}