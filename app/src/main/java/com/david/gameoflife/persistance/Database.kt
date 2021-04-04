package com.david.gameoflife.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.david.gameoflife.utils.SingletonHolder

@Database(entities = [Board::class, Construct::class], version = 10)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boardDao(): BoardDao
    abstract fun constructDao(): ConstructDao

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "gameoflife.db").fallbackToDestructiveMigration().build()
    })
}

