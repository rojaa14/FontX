package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fonts")
data class FontEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val filePath: String, // Path in internal filesDir (or "preset_monospace", etc.)
    val fileSize: String,
    val styleCategory: String, // "Sans-Serif", "Serif", "Monospace", "Cursive", "Display"
    val isInstalled: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)
