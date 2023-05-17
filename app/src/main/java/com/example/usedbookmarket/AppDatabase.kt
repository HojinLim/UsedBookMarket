package com.example.usedbookmarket

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.usedbookmarket.dao.HistoryDao
import com.example.usedbookmarket.model.History

@Database(entities = [History::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
