package retanar.timerquit.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retanar.timerquit.data.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room
        .databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = AppDatabase.DB_NAME,
        )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideTimesDao(db: AppDatabase) = db.dao
}
