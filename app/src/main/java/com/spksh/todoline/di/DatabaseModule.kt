package com.spksh.todoline.di

import android.content.Context
import androidx.room.Room
import com.spksh.todoline.data.Task.TaskDao
import com.spksh.todoline.data.AppDatabase
import com.spksh.todoline.data.Event.EventDao
import com.spksh.todoline.data.Tag.TagDao
import com.spksh.todoline.data.TimeLinedActivity.TimeLinedActivityDao
import com.spksh.todoline.data.TimeSlot.TimeSlotDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "task_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideTagDao(database: AppDatabase): TagDao {
        return database.tagDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: AppDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun provideTimeLinedActivityDao(database: AppDatabase): TimeLinedActivityDao {
        return database.timeLinedActivityDao()
    }

    @Provides
    @Singleton
    fun provideTimeSlotDao(database: AppDatabase): TimeSlotDao {
        return database.timeSlotDao()
    }
}