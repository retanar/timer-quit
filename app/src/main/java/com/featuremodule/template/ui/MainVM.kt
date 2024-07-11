package com.featuremodule.template.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class MainVM @Inject constructor(
) : ViewModel() {
    val timeCards = mutableStateOf(emptyList<TimeCardState>())

    init {
        timeCards.value = List(size = 3) { TimeCardState(it.toString(), "${it}d") }
    }
}
