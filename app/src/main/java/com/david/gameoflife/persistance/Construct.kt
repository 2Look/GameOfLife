package com.david.gameoflife.persistance

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity
data class Construct(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "coordinates") val coordinates: String
)

@Dao
interface ConstructDao {
    @Query("SELECT * FROM Construct")
    suspend fun getAll(): List<Construct>

    @Update
    fun updateConstruct(construct: Construct)

    @Insert
    suspend fun insertConstruct(construct: Construct): Long

    @Delete
    suspend fun deleteConstruct(construct: Construct)
}

