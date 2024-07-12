package retanar.timerquit.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retanar.timerquit.data.TimesDao
import javax.inject.Inject

@HiltViewModel
internal class MainVM @Inject constructor(
    dao: TimesDao,
) : ViewModel() {
    private val timeEntities = dao.getAllFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )
    val timeCards = mutableStateOf(emptyList<TimeCardState>())

    init {
        viewModelScope.launch {
            timeEntities.collectLatest {
                updateCardStates()
            }
        }
    }

    private fun updateCardStates() {
        val currentTime = System.currentTimeMillis()
        timeCards.value = timeEntities.value.map { entity ->
            TimeCardState(
                title = entity.title,
                timeString = (currentTime - entity.timeUtcMs).toString(),
                record = entity.longestTimeMs.takeUnless { it == 0L }?.toString()
            )
        }
    }
}
