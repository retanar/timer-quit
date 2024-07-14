package retanar.timerquit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DialogController(
    dialogType: DialogType,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    onRefresh: (TimeCardState) -> Unit,
    onDelete: (TimeCardState) -> Unit,
) = when (dialogType) {
    DialogType.None -> {}
    DialogType.Add -> AddDialog(onDismiss, onAdd)
    is DialogType.ResetConfirmation -> ResetConfirmation(dialogType, onDismiss, onRefresh)
    is DialogType.DeleteConfirmation -> DeleteConfirmation(dialogType, onDismiss, onDelete)
}

@Composable
private fun AddDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
) = Dialog(onDismissRequest = onDismiss) {
    var text by remember { mutableStateOf("") }
    Card {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(text = "Add tracker")
            TextField(value = text, onValueChange = { text = it })

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(text = "Cancel")
                }
                Button(
                    onClick = {
                        onAdd(text)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = "Add")
                }
            }
        }
    }
}

@Composable
private fun ResetConfirmation(
    dialogType: DialogType.ResetConfirmation,
    onDismiss: () -> Unit,
    onRefresh: (TimeCardState) -> Unit,
) = Dialog(onDismissRequest = onDismiss) {
    Card {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(text = "Do you want to reset time for ${dialogType.state.title}?")

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(text = "No")
                }
                Button(
                    onClick = {
                        onRefresh(dialogType.state)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = "Yes")
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmation(
    dialogType: DialogType.DeleteConfirmation,
    onDismiss: () -> Unit,
    onDelete: (TimeCardState) -> Unit,
) = Dialog(onDismissRequest = onDismiss) {
    Card {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(text = "Do you want to delete ${dialogType.state.title}?")

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(text = "No")
                }
                Button(
                    onClick = {
                        onDelete(dialogType.state)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = "Yes")
                }
            }
        }
    }
}
