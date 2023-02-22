package com.example.usedbookmarket

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.usedbookmarket.dao.HistoryDao
import com.example.usedbookmarket.model.History

@Database(entities = [History::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
/*
fun getAppDatabase(context: Context): AppDatabase {

    val migration_1_2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT," + "PRIMARY KEY(`id`))")
        }

    }

    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchDB"
    )
        .addMigrations(migration_1_2)
        .build()
}
 */