package com.david.gameoflife.persistance

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update

@Entity
data class Board(
    @PrimaryKey(autoGenerate = true) val boardId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "coordinates") val coordinates: String
)

@Dao
interface BoardDao {

    @Query("SELECT * FROM Board")
    suspend fun getAll(): List<Board>

    @Update
    suspend fun updateBoard(board: Board)

    @Insert
    suspend fun insertBoard(board: Board): Long

    @Delete
    suspend fun deleteBoard(board: Board)
}