package retanar.timerquit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TimesDao {
    @Query("SELECT * FROM ${TimeEntity.TABLE_NAME}")
    suspend fun getAll(): List<TimeEntity>

    @Query("SELECT * FROM ${TimeEntity.TABLE_NAME}")
    fun getAllFlow(): Flow<List<TimeEntity>>

    @Insert
    suspend fun insert(entity: TimeEntity)

    @Delete
    suspend fun delete(entity: TimeEntity)

    @Update
    suspend fun update(entity: TimeEntity)
}
