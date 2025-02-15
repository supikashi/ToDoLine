package com.spksh.todoline

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.spksh.todoline.data.TaskDatabase
import com.spksh.todoline.data.TaskRepository

class App : Application() {
    lateinit var database: TaskDatabase
        private set

    lateinit var repository: TaskRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "task_database"
        ).addMigrations(MIGRATION_2_3)
            .addMigrations(MIGRATION_3_4)
            //.addMigrations(MIGRATION_4_5)
            .build()

        repository = TaskRepository(database.taskDao())
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