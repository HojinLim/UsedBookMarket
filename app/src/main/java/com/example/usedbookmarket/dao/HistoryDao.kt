package com.example.usedbookmarket.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.usedbookmarket.model.History
import androidx.room.Query

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history WHERE keyword = :keyword")
    fun delete(keyword: String)

}