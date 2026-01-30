package com.example.phantom.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.phantom.domain.model.UserBehaviorSnapshot
import com.example.phantom.domain.model.DefensiveAction
import com.example.phantom.util.Converters
import android.content.Context

@Database(
    entities = [UserBehaviorSnapshot::class, DefensiveAction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun behaviorDao(): BehaviorDao
    abstract fun defensiveActionDao(): DefensiveActionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "phantom_database"
                )
                .fallbackToDestructiveMigration() // For development only
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}