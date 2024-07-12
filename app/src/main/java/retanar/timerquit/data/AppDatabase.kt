package retanar.timerquit.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TimeEntity::class], exportSchema = false, version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val dao: TimesDao

    companion object {
        const val DB_NAME = "app_database"
    }
}
