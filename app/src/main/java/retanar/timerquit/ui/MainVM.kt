package retanar.timerquit.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retanar.timerquit.data.TimeEntity
import retanar.timerquit.data.TimesDao
import javax.inject.Inject
import kotlin.math.max
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
            // Auto update on database changes
            timeEntities.collectLatest {
                updateCardStates()
            }
        }
        viewModelScope.launch {
            // Timer update
            while (true) {
                delay(1000)
                updateCardStates()
            }
        }
    }

    private fun updateCardStates() {
        val currentTime = System.currentTimeMillis()
        timeCards.value = timeEntities.value.map { entity ->
            TimeCardState(
                id = entity.id,
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

    fun refreshTime(state: TimeCardState) = viewModelScope.launch {
        timeEntities.value.find { it.id == state.id }?.let { entity ->
            // replace record if it was beaten
            val record = max(System.currentTimeMillis() - entity.timeUtcMs, entity.longestTimeMs)
            dao.update(entity.copy(timeUtcMs = System.currentTimeMillis(), longestTimeMs = record))
        }
    }

    fun deleteTime(state: TimeCardState) = viewModelScope.launch {
        timeEntities.value.find { it.id == state.id }?.let { entity ->
            dao.delete(entity)
        }
    }
}

sealed interface DialogType {
    data object Add : DialogType
    data class ResetConfirmation(val state: TimeCardState) : DialogType
    data class DeleteConfirmation(val state: TimeCardState) : DialogType
    data object None : DialogType
}
