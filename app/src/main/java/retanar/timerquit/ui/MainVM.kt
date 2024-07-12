package retanar.timerquit.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retanar.timerquit.data.TimeEntity
import retanar.timerquit.data.TimesDao
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
internal class MainVM @Inject constructor(
    private val dao: TimesDao,
) : ViewModel() {
    private val timeEntities = dao.getAllFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )
    val timeCards = mutableStateOf(emptyList<TimeCardState>())
    val dialogType = mutableStateOf<DialogType>(DialogType.None)

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
                timeString = (currentTime - entity.timeUtcMs).durationString(),
                record = entity.longestTimeMs.takeUnless { it == 0L }?.durationString(),
            )
        }
    }

    fun setDialog(type: DialogType) {
        dialogType.value = type
    }

    fun addTime(title: String) = viewModelScope.launch {
        dao.insert(
            TimeEntity(
                title = title,
                timeUtcMs = System.currentTimeMillis(),
            ),
        )
    }

    private fun Long.durationString() =
        milliseconds.toComponents { days, hours, minutes, seconds, _ ->
            "${days}d ${hours}h ${minutes}m ${seconds}s"
        }
}

sealed interface DialogType {
    data object Add : DialogType
    data object None : DialogType
}
