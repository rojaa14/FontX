package com.example.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class FontRepository(private val fontDao: FontDao) {
    val allFonts: Flow<List<FontEntity>> = fontDao.getAllFonts()

    suspend fun getFontById(id: Int): FontEntity? = fontDao.getFontById(id)

    suspend fun insertFont(font: FontEntity): Long = fontDao.insertFont(font)

    suspend fun deleteFont(context: Context, font: FontEntity) {
        withContext(Dispatchers.IO) {
            // Check if file exists in internal storage, if so, delete it
            if (!font.filePath.startsWith("preset_")) {
                val file = File(font.filePath)
                if (file.exists()) {
                    file.delete()
                }
            }
            fontDao.deleteFont(font)
        }
    }

    suspend fun applyFont(id: Int) {
        withContext(Dispatchers.IO) {
            // First disable all other fonts as installed (only one custom font active)
            fontDao.clearAllInstalledStatus()
            // Set this one as active
            fontDao.updateInstalledStatus(id, true)
        }
    }

    suspend fun deactivateFont(id: Int) {
        withContext(Dispatchers.IO) {
            fontDao.updateInstalledStatus(id, false)
        }
    }

    suspend fun importFontFile(
        context: Context,
        uri: Uri,
        fontName: String,
        category: String
    ): FontEntity? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            
            if (inputStream == null) return@withContext null

            // Determine file size
            var rawSize: Long = 0
            val sizeStr: String = try {
                contentResolver.openAssetFileDescriptor(uri, "r")?.use {
                    rawSize = it.length
                    val sizeKb = rawSize / 1024
                    if (sizeKb > 1024) {
                        String.format("%.1f MB", sizeKb / 1024.0)
                    } else {
                        "$sizeKb KB"
                    }
                } ?: run {
                    "Unknown Size"
                }
            } catch (e: Exception) {
                "Unknown Size"
            }

            // Create directories if needed
            val fontsDir = File(context.filesDir, "fonts")
            if (!fontsDir.exists()) {
                fontsDir.mkdirs()
            }

            // Define target file name using UUID to avoid collisions
            val uniqueFileName = "font_${UUID.randomUUID()}.ttf"
            val targetFile = File(fontsDir, uniqueFileName)

            // Copy file content
            FileOutputStream(targetFile).use { outputStream ->
                inputStream.use { input ->
                    input.copyTo(outputStream)
                }
            }

            // Verify file actually exists and has size
            if (targetFile.exists() && targetFile.length() > 0) {
                val actualSize = targetFile.length() / 1024
                val finalSizeStr = if (actualSize > 1024) {
                    String.format("%.1f MB", actualSize / 1024.0)
                } else {
                    "$actualSize KB"
                }

                // Return model
                FontEntity(
                    name = fontName.trim().ifEmpty { "Custom Font" },
                    filePath = targetFile.absolutePath,
                    fileSize = finalSizeStr,
                    styleCategory = category,
                    isInstalled = false
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
