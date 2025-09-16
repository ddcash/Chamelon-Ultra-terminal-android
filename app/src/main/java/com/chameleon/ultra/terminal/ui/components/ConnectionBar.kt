package com.chameleon.ultra.terminal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chameleon.ultra.terminal.communication.ConnectionManager
import com.chameleon.ultra.terminal.communication.ConnectionType
import kotlinx.coroutines.launch

@Composable
fun ConnectionBar(
    connectionManager: ConnectionManager,
    modifier: Modifier = Modifier
) {
    val connectionState by connectionManager.connectionState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connection status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (statusColor, statusText) = when (connectionState) {
                    is ConnectionManager.ConnectionState.Connected -> {
                        Color.Green to "Connected"
                    }
                    is ConnectionManager.ConnectionState.Connecting -> {
                        Color.Yellow to "Connecting..."
                    }
                    is ConnectionManager.ConnectionState.Disconnected -> {
                        Color.Gray to "Disconnected"
                    }
                    is ConnectionManager.ConnectionState.Error -> {
                        Color.Red to "Error: ${(connectionState as ConnectionManager.ConnectionState.Error).message}"
                    }
                }

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, RoundedCornerShape(6.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (connectionManager.isConnected()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = when (connectionManager.getConnectionType()) {
                            ConnectionType.USB -> Icons.Default.Cable
                            ConnectionType.BLUETOOTH -> Icons.Default.Bluetooth
                            else -> Icons.Default.Cable
                        },
                        contentDescription = connectionManager.getConnectionType().name,
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Connection buttons
            Row {
                if (connectionManager.isConnected()) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                connectionManager.disconnect()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Disconnect")
                    }
                } else {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                connectionManager.connectUsb()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cable,
                            contentDescription = "USB",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("USB")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                connectionManager.connectBluetooth()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bluetooth,
                            contentDescription = "Bluetooth",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("BT")
                    }
                }
            }
        }
    }
}