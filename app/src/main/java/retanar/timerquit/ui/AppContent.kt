package retanar.timerquit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun AppContent(viewModel: MainVM = hiltViewModel()) {
    val timeCards by viewModel.timeCards

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.setDialog(DialogType.Add) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        // Remove this and status bar coloring in AppTheme for edge to edge
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(timeCards) { cardState ->
                TimeCard(state = cardState, openDialog = viewModel::setDialog)
            }
        }
    }

    DialogController(
        dialogType = viewModel.dialogType.value,
        onDismiss = { viewModel.setDialog(DialogType.None) },
        onAdd = viewModel::addTime,
        onRefresh = viewModel::refreshTime,
    )
}

@Composable
private fun TimeCard(state: TimeCardState, openDialog: (DialogType) -> Unit) {
    Card(
        modifier = Modifier.padding(all = 4.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.padding(all = 16.dp),
            ) {
                Text(text = state.title)
                state.record?.let { record ->
                    Text(text = "Record $record")
                }
                Text(text = state.timeString)
            }

            IconButton(onClick = { openDialog(DialogType.ResetTime(state)) }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh time")
            }
        }
    }
}

@Composable
private fun DialogController(
    dialogType: DialogType,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    onRefresh: (TimeCardState) -> Unit,
) = when (dialogType) {
    DialogType.None -> {}
    DialogType.Add -> Dialog(onDismissRequest = onDismiss) {
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

    is DialogType.ResetTime -> Dialog(onDismissRequest = onDismiss) {
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
}
