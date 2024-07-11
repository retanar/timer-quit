package retanar.timerquit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import retanar.timerquit.core.ui.theme.AppTheme
import retanar.timerquit.ui.AppContent

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Status and navigation bars can be adjusted here
        enableEdgeToEdge()

        setContent {
            AppTheme {
                AppContent()
            }
        }
    }
}
