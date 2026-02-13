package com.meetrennes.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.meetrennes.app.domain.Lieu

@Database(
    entities = [Lieu::class],
    version = 1,
    exportSchema = false
)
abstract class MeetRennesDatabase : RoomDatabase() {

    abstract fun lieuDao(): LieuDao

    companion object {
        fun create(context: Context): MeetRennesDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MeetRennesDatabase::class.java,
                "meetrennes_database"
            ).build()
        }
    }
}
