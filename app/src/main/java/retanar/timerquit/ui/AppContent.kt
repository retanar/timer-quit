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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun AppContent(viewModel: MainVM = hiltViewModel()) {
//    val navController = rememberNavController()
//    val backStackEntry by navController.currentBackStackEntryAsState()

    val timeCards by viewModel.timeCards

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        // Remove this and status bar coloring in AppTheme for edge to edge-
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(timeCards) { cardState ->
                TimeCard(cardState)
            }
        }
    }
}

@Composable
private fun TimeCard(state: TimeCardState) {
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
                Text(text = state.timeString)
            }

            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh time")
            }
        }
    }
}
