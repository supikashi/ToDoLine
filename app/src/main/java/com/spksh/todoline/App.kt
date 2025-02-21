package com.spksh.todoline

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.spksh.todoline.data.DataStoreRepository
import com.spksh.todoline.data.TaskDatabase
import com.spksh.todoline.data.TaskRepository

class App : Application() {
    private lateinit var database: TaskDatabase

    lateinit var taskRepository: TaskRepository
        private set

    //private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    //lateinit var dataStoreRepository: DataStoreRepository
    //    private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "task_database"
        )//.addMigrations(MIGRATION_2_3)
            //.addMigrations(MIGRATION_3_4)
            //.addMigrations(MIGRATION_4_5)
            //.addMigrations(MIGRATION_5_6)
            .build()

        taskRepository = TaskRepository(database.taskDao())
        //dataStoreRepository = DataStoreRepository(dataStore)
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE task_table ADD COLUMN description TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE task_table ADD COLUMN deadline INTEGER")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE task_table ADD COLUMN tagsIds TEXT NOT NULL DEFAULT ''")
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS tag_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                color TEXT NOT NULL
            )
            """.trimIndent())
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE tag_table ADD COLUMN show INTEGER NOT NULL DEFAULT 1")
    }
}