package com.david.gameoflife

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ConfirmDialog(
    question: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Card(Modifier.padding(8.dp)) {
            Column(Modifier.padding(4.dp)) {
                Text(question, modifier = Modifier.padding(PaddingValues(8.dp, 4.dp)))
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(4.dp)
                ) {
                    Button(
                        modifier = Modifier.padding(PaddingValues(top = 4.dp, end = 4.dp)),
                        onClick = onCancel
                    ) {
                        Text(cancelText)
                    }
                    Button(
                        modifier = Modifier.padding(PaddingValues(top = 4.dp)),
                        onClick = onConfirm
                    ) {
                        Text(confirmText)
                    }
                }
            }

        }
    }
}

@Composable
fun TextBoxDialog(
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    placeholderText: String,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var newConstructName: String by remember { mutableStateOf("") }
//    var enabled: Boolean by remember { mutableStateOf(true) }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(Modifier.padding(8.dp)) {
            Column(Modifier.padding(4.dp)) {
                TextField(
                    modifier = Modifier.padding(PaddingValues(4.dp)),
                    value = newConstructName,
//                    enabled = enabled,
                    onValueChange = { newConstructName = it },
                    placeholder = {
                        Text(placeholderText)
                    })
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(4.dp)
                ) {
                    Button(
                        modifier = Modifier.padding(PaddingValues(top = 4.dp, end = 4.dp)),
                        onClick = {
                            onCancel()
                            newConstructName = ""
//                            enabled = false
                        }
                    ) {
                        Text(cancelText)
                    }
                    Button(
                        modifier = Modifier.padding(PaddingValues(top = 4.dp)),
                        onClick = {
                            onConfirm(newConstructName)
                            newConstructName = ""
//                            enabled = false
                        }
                    ) {
                        Text(confirmText)
                    }
                }
            }
        }
    }
}
