package com.chameleon.ultra.terminal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chameleon.ultra.terminal.terminal.TerminalLineType
import com.chameleon.ultra.terminal.terminal.TerminalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel,
    modifier: Modifier = Modifier
) {
    val terminalOutput by viewModel.terminalOutput.collectAsState()
    val currentInput by viewModel.currentInput.collectAsState()
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    // Auto-scroll to bottom when new output is added
    LaunchedEffect(terminalOutput.size) {
        if (terminalOutput.isNotEmpty()) {
            listState.animateScrollToItem(terminalOutput.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Terminal output
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(terminalOutput) { line ->
                val textColor = when (line.type) {
                    TerminalLineType.COMMAND -> Color.Cyan
                    TerminalLineType.RESPONSE -> Color.Green
                    TerminalLineType.ERROR -> Color.Red
                    TerminalLineType.INFO -> Color.Yellow
                    TerminalLineType.OUTPUT -> Color.White
                }

                Text(
                    text = line.text,
                    color = textColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Command input
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = currentInput,
                    onValueChange = viewModel::updateInput,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                            "Enter command...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            viewModel.executeCommand(currentInput)
                        }
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        viewModel.executeCommand(currentInput)
                    },
                    enabled = currentInput.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (currentInput.isNotBlank()) Color.Green else Color.Gray
                    )
                }
            }
        }
    }

    // Request focus on terminal input when screen loads
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}