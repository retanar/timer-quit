package retanar.timerquit.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.SharedFlow

/**
 * Common usage is:
 * ```
 *  sharedFlow.CollectWithLifecycle {
 *      doSomething(it)
 *  }
 * ```
 */
@Composable
fun <T> SharedFlow<T>.CollectWithLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: (T) -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val updatedCollector by rememberUpdatedState(newValue = collector)

    LaunchedEffect(this, lifecycle) {
        lifecycle.repeatOnLifecycle(state) {
            collect(updatedCollector)
        }
    }
}
