package com.chameleon.ultra.terminal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.chameleon.ultra.terminal.communication.ConnectionManager
import com.chameleon.ultra.terminal.device.ChameleonUltraDevice
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiScreen(
    connectionManager: ConnectionManager,
    chameleonDevice: ChameleonUltraDevice,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedSlot by remember { mutableStateOf(0) }
    var uidInput by remember { mutableStateOf("") }
    var scanResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var isScanning by remember { mutableStateOf(false) }
    var deviceVersion by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Device Info Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Device Information",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (deviceVersion.isNotEmpty()) "Version: $deviceVersion" else "Version: Unknown",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val version = chameleonDevice.getVersion()
                                    deviceVersion = version ?: "Failed to get version"
                                }
                            },
                            enabled = connectionManager.isConnected()
                        ) {
                            Text("Get Version")
                        }
                    }
                }
            }
        }

        item {
            // Slot Management Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Slot Management",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Active Slot: $selectedSlot",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(8) { slot ->
                            FilterChip(
                                onClick = {
                                    selectedSlot = slot
                                    coroutineScope.launch {
                                        chameleonDevice.setSlot(slot)
                                    }
                                },
                                label = { Text(slot.toString()) },
                                selected = selectedSlot == slot,
                                enabled = connectionManager.isConnected()
                            )
                        }
                    }
                }
            }
        }

        item {
            // UID Configuration Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "UID Configuration",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uidInput,
                        onValueChange = { uidInput = it.filter { char -> char.isDigit() || char.lowercaseChar() in 'a'..'f' } },
                        label = { Text("UID (Hex)") },
                        placeholder = { Text("e.g., 04AB1234") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val success = chameleonDevice.setUid(uidInput)
                                // Handle result (show snackbar or update UI)
                            }
                        },
                        enabled = connectionManager.isConnected() && uidInput.isNotEmpty(),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Set UID"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Set UID")
                    }
                }
            }
        }

        item {
            // HF Scan Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HF Scan",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = {
                                isScanning = true
                                coroutineScope.launch {
                                    try {
                                        val results = chameleonDevice.scanHf14a()
                                        scanResults = results ?: listOf("No tags found")
                                    } catch (e: Exception) {
                                        scanResults = listOf("Scan failed: ${e.message}")
                                    } finally {
                                        isScanning = false
                                    }
                                }
                            },
                            enabled = connectionManager.isConnected() && !isScanning
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Scan"
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isScanning) "Scanning..." else "Scan HF")
                        }
                    }

                    if (scanResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Results:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        scanResults.forEach { result ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = result,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Quick Actions Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                // Start emulation
                            },
                            enabled = connectionManager.isConnected(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Start Emulation")
                        }

                        Button(
                            onClick = {
                                // Stop emulation
                            },
                            enabled = connectionManager.isConnected(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Stop Emulation")
                        }
                    }
                }
            }
        }
    }
}