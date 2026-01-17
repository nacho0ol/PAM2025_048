package com.example.scentra.uicontroller.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DynamicNotesInput(
    label: String,
    currentValue: String,
    onValueChange: (String) -> Unit
) {
    val notesList = remember(currentValue) {
        if (currentValue.isBlank()) emptyList()
        else currentValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    var textInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (notesList.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                notesList.forEach { note ->
                    InputChip(
                        selected = true,
                        onClick = {
                            val newList = notesList.toMutableList()
                            newList.remove(note)
                            onValueChange(newList.joinToString(", "))
                        },
                        label = { Text(note) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = InputChipDefaults.inputChipColors(
                            containerColor = Color(0xFF1D1B20),
                            labelColor = Color.White,
                            trailingIconColor = Color.White,

                            selectedContainerColor = Color(0xFF1D1B20),
                            selectedLabelColor = Color.White,
                            selectedTrailingIconColor = Color.White
                        ),
                        border = null
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text("Tambah $label...") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (textInput.isNotBlank()) {
                            val newList = notesList.toMutableList()
                            newList.add(textInput.trim())
                            onValueChange(newList.joinToString(", "))
                            textInput = ""
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        val newList = notesList.toMutableList()
                        newList.add(textInput.trim())
                        onValueChange(newList.joinToString(", "))
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .padding(top = 6.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF1D1B20),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    }
}