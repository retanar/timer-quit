package com.featuremodule.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface UiState
interface UiEvent

abstract class BaseVM<State : UiState, Event : UiEvent> : ViewModel() {
    // Lazy on both states is needed to pass parameters from child ViewModel's dependencies
    // to initialState(). Otherwise initialState() will be called before child ViewModel has time to
    // run the constructor (and receive dependencies).
    private val _state: MutableStateFlow<State> by lazy { MutableStateFlow(initialState()) }

    /** State to be consumed by UI */
    val state by lazy { _state.asStateFlow() }

    protected abstract fun initialState(): State

    @Synchronized
    protected fun setState(action: State.() -> State) {
        _state.value = _state.value.action()
    }

    // Extra capacity added to possibly make emit suspend less
    private val events = MutableSharedFlow<Event>(extraBufferCapacity = 1)

    /** Allows UI to send events to VM */
    fun postEvent(event: Event) = launch {
        events.emit(event)
    }

    init {
        // Run collection of events
        launch {
            events.collect(::handleEvent)
        }
    }

    /** Receives events in VM */
    protected abstract fun handleEvent(event: Event)

    /** Utility function mirroring viewModelScope.launch() */
    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job =
        viewModelScope.launch(block = block)

    /** Utility property to be used in logging */
    protected val tag: String
        get() = this::class.simpleName ?: "BaseVM"
}
