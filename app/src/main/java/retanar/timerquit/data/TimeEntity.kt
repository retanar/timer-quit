package retanar.timerquit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(TimeEntity.TABLE_NAME)
data class TimeEntity(
    val title: String,
    val timeUtcMs: Long,
    /** Set to 0 if longest time was not recorded yet. */
    val longestTimeMs: Long = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
) {
    companion object {
        const val TABLE_NAME = "times"
    }
}
