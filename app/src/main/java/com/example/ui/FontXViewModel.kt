package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.FontEntity
import com.example.data.FontRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    data class Detail(val fontId: Int) : Screen()
    object ShizukuGuide : Screen()
    object About : Screen()
}

enum class ShizukuStatus {
    NOT_INSTALLED,
    CONNECTED,
    NOT_RUNNING
}

enum class CompilingState {
    IDLE,
    EXTRACTING,
    INJECTING,
    SIGNING,
    SUCCESS,
    ERROR
}

class FontXViewModel(private val repository: FontRepository) : ViewModel() {

    // Simple navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Shizuku connection state
    private val _shizukuStatus = MutableStateFlow(ShizukuStatus.NOT_INSTALLED)
    val shizukuStatus: StateFlow<ShizukuStatus> = _shizukuStatus.asStateFlow()

    // Compilation progress
    private val _compilingState = MutableStateFlow(CompilingState.IDLE)
    val compilingState: StateFlow<CompilingState> = _compilingState.asStateFlow()

    private val _compilingProgress = MutableStateFlow(0f)
    val compilingProgress: StateFlow<Float> = _compilingProgress.asStateFlow()

    private val _compilingLog = MutableStateFlow("")
    val compilingLog: StateFlow<String> = _compilingLog.asStateFlow()

    // Search query for fonts
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active detail font
    private val _selectedFont = MutableStateFlow<FontEntity?>(null)
    val selectedFont: StateFlow<FontEntity?> = _selectedFont.asStateFlow()

    // Observe all database fonts
    val allFonts: StateFlow<List<FontEntity>> = repository.allFonts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Automatically transition from Splash to Home after a short delay
        viewModelScope.launch {
            delay(1500)
            _currentScreen.value = Screen.Home
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        if (screen is Screen.Detail) {
            viewModelScope.launch {
                _selectedFont.value = repository.getFontById(screen.fontId)
                _compilingState.value = CompilingState.IDLE
                _compilingLog.value = ""
                _compilingProgress.value = 0f
            }
        }
    }

    fun checkShizuku(context: Context) {
        viewModelScope.launch {
            val isInstalled = isPackageInstalled("moe.shizuku.privileged.api", context)
            if (!isInstalled) {
                _shizukuStatus.value = ShizukuStatus.NOT_INSTALLED
            } else {
                // In actual Shizuku, we check bindings. Here, we can simulate checking
                // the service connection or run background check.
                // Since this app runs offline, we simulate the binder check:
                val isRunningService = (1..2).random() == 1 // simulate connection
                if (isRunningService) {
                    _shizukuStatus.value = ShizukuStatus.CONNECTED
                } else {
                    _shizukuStatus.value = ShizukuStatus.NOT_RUNNING
                }
            }
        }
    }

    private fun isPackageInstalled(packageName: String, context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun importFont(context: Context, uri: Uri, fontName: String, category: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val model = repository.importFontFile(context, uri, fontName, category)
            if (model != null) {
                repository.insertFont(model)
                onSuccess()
            } else {
                onError("Failed to read or save TTF file.")
            }
        }
    }

    fun deleteFont(context: Context, font: FontEntity) {
        viewModelScope.launch {
            repository.deleteFont(context, font)
            if (_selectedFont.value?.id == font.id) {
                _selectedFont.value = null
                _currentScreen.value = Screen.Home
            }
        }
    }

    fun applyFont(fontId: Int) {
        viewModelScope.launch {
            repository.applyFont(fontId)
            // Refresh detailed view model
            _selectedFont.value = repository.getFontById(fontId)
        }
    }

    fun deactivateFont(fontId: Int) {
        viewModelScope.launch {
            repository.deactivateFont(fontId)
            _selectedFont.value = repository.getFontById(fontId)
        }
    }

    // High fidelity offline mock compilation process representing real APK font compiling!
    fun runApkCompiler(fontName: String) {
        viewModelScope.launch {
            _compilingState.value = CompilingState.EXTRACTING
            _compilingProgress.value = 0.15f
            _compilingLog.value = "Starting font compilation for \"$fontName\"...\n"
            delay(800)

            _compilingLog.value += "[1/5] Extracting assets/font_template.apk...\n"
            _compilingProgress.value = 0.3f
            delay(1000)

            _compilingState.value = CompilingState.INJECTING
            _compilingLog.value += "[2/5] Injecting font file into apk internal assets/fonts/ ...\n"
            _compilingProgress.value = 0.5f
            delay(1200)

            _compilingLog.value += "[3/5] Re-indexing font overlay resources...\n"
            _compilingProgress.value = 0.7f
            delay(800)

            _compilingState.value = CompilingState.SIGNING
            _compilingLog.value += "[4/5] Aligning ZIP blocks & signing custom APK using built-in One UI certificate...\n"
            _compilingProgress.value = 0.85f
            delay(1500)

            _compilingState.value = CompilingState.SUCCESS
            _compilingLog.value += "[5/5] Success! FontX package \"com.fontx.overlay.${fontName.lowercase().replace(" ", "")}\" compiled successfully offline.\nReady for Shizuku or wireless install."
            _compilingProgress.value = 1.0f
        }
    }

    // Pre-insert preset fonts on database create if empty
    fun insertPresetFontsIfEmpty(context: Context) {
        viewModelScope.launch {
            if (allFonts.value.isEmpty()) {
                val presets = listOf(
                    FontEntity(
                        name = "Space Grotesk Pro",
                        filePath = "preset_monospace",
                        fileSize = "76 KB",
                        styleCategory = "Monospace",
                        isInstalled = false
                    ),
                    FontEntity(
                        name = "Elegant Serif",
                        filePath = "preset_serif",
                        fileSize = "112 KB",
                        styleCategory = "Serif",
                        isInstalled = false
                    ),
                    FontEntity(
                        name = "Modernist Sans",
                        filePath = "preset_sans",
                        fileSize = "64 KB",
                        styleCategory = "Sans-Serif",
                        isInstalled = true // Mark one preset as installed for visual fidelity on start!
                    ),
                    FontEntity(
                        name = "Playful Script",
                        filePath = "preset_cursive",
                        fileSize = "95 KB",
                        styleCategory = "Cursive",
                        isInstalled = false
                    )
                )
                for (preset in presets) {
                    repository.insertFont(preset)
                }
            }
        }
    }
}

class FontXViewModelFactory(private val repository: FontRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FontXViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FontXViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
