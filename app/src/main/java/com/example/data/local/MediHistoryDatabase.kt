package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PatientEntity::class,
        RecordEntity::class,
        ExtractedItemEntity::class,
        SummaryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MediHistoryDatabase : RoomDatabase() {

    abstract fun mediHistoryDao(): MediHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: MediHistoryDatabase? = null

        fun getDatabase(context: Context): MediHistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediHistoryDatabase::class.java,
                    "medihistory_ai.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
