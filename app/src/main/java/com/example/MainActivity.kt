package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.FontRepository
import com.example.ui.FontXApp
import com.example.ui.FontXViewModel
import com.example.ui.FontXViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Local SQLite Database (Room) & Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = FontRepository(database.fontDao())

        // Initialize MVVM ViewModel using Custom Factory
        val viewModel = ViewModelProvider(
            this,
            FontXViewModelFactory(repository)
        )[FontXViewModel::class.java]

        // Load Preset Fonts if Database is Empty and Trigger Status Checks
        viewModel.insertPresetFontsIfEmpty(applicationContext)
        viewModel.loadPreferences(applicationContext)
        viewModel.checkShizuku(applicationContext)

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }
            MyApplicationTheme(darkTheme = darkTheme) {
                FontXApp(viewModel)
            }
        }
    }
}
