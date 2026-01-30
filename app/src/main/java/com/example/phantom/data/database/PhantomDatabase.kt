package com.example.phantom.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.phantom.data.model.BehavioralPattern
import com.example.phantom.data.model.DefensiveAction
import com.example.phantom.data.model.TrustScore
import com.example.phantom.util.Converters
import android.content.Context

@Database(
    entities = [BehavioralPattern::class, TrustScore::class, DefensiveAction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PhantomDatabase : RoomDatabase() {
    abstract fun behavioralDao(): BehavioralDao
    
    companion object {
        @Volatile
        private var INSTANCE: PhantomDatabase? = null
        
        fun getDatabase(context: Context): PhantomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhantomDatabase::class.java,
                    "phantom_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}