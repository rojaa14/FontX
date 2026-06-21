package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FontDao {
    @Query("SELECT * FROM fonts ORDER BY addedAt DESC")
    fun getAllFonts(): Flow<List<FontEntity>>

    @Query("SELECT * FROM fonts WHERE id = :id LIMIT 1")
    suspend fun getFontById(id: Int): FontEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFont(font: FontEntity): Long

    @Delete
    suspend fun deleteFont(font: FontEntity)

    @Query("UPDATE fonts SET isInstalled = :isInstalled WHERE id = :id")
    suspend fun updateInstalledStatus(id: Int, isInstalled: Boolean)

    @Query("UPDATE fonts SET isInstalled = 0")
    suspend fun clearAllInstalledStatus()
}
