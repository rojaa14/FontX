package com.example

import android.content.pm.PackageManager
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
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FontXViewModel

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        viewModel.onShizukuBinderReceived(this)
    }
    
    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        viewModel.onShizukuBinderDead()
    }
    
    private val requestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            viewModel.onShizukuPermissionGranted(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Local SQLite Database (Room) & Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = FontRepository(database.fontDao())

        // Initialize MVVM ViewModel using Custom Factory
        viewModel = ViewModelProvider(
            this,
            FontXViewModelFactory(repository)
        )[FontXViewModel::class.java]

        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)

        // Load Preset Fonts if Database is Empty and Trigger Status Checks
        viewModel.insertPresetFontsIfEmpty(applicationContext)
        viewModel.loadPreferences(applicationContext)
        viewModel.checkShizukuReal(this)

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

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
    }
}
