package retanar.timerquit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import retanar.timerquit.core.ui.theme.AppTheme
import retanar.timerquit.ui.DialogType.None

@Composable
internal fun AppContent(viewModel: MainVM = hiltViewModel()) {
    val timeCards by viewModel.timeCards

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.setDialog(DialogType.Add) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(timeCards) { cardState ->
                TimeCard(state = cardState, openDialog = viewModel::setDialog)
            }
        }
    }

    DialogController(
        dialogType = viewModel.dialogType.value,
        onDismiss = { viewModel.setDialog(None) },
        onAdd = viewModel::addTime,
        onRefresh = viewModel::refreshTime,
        onDelete = viewModel::deleteTime,
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
                Text(text = state.title, style = MaterialTheme.typography.titleLarge)
                state.record?.let { record ->
                    Text(text = "Record $record", style = MaterialTheme.typography.bodyMedium)
                }
                Text(text = state.timeString, style = MaterialTheme.typography.titleMedium)
            }

            Column {
                IconButton(onClick = { openDialog(DialogType.DeleteConfirmation(state)) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete timer")
                }
                IconButton(onClick = { openDialog(DialogType.ResetConfirmation(state)) }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh time")
                }
            }
        }
    }
}

@Preview
@Composable
private fun TimeCardPreview() {
    AppTheme {
        TimeCard(
            state = TimeCardState(
                title = "Some title",
                timeString = "1d 0h 40m 55s",
                record = "0d 2h 15m 20s",
            ),
            openDialog = {},
        )
    }
}
