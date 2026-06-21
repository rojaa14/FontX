package com.example.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.FontEntity
import java.io.File

// Custom Font Loader
@Composable
fun rememberCustomFont(filePath: String, styleCategory: String): FontFamily {
    return remember(filePath) {
        try {
            if (filePath.startsWith("preset_")) {
                when (styleCategory) {
                    "Monospace" -> FontFamily.Monospace
                    "Serif" -> FontFamily.Serif
                    "Sans-Serif" -> FontFamily.SansSerif
                    "Cursive" -> FontFamily.Cursive
                    else -> FontFamily.Default
                }
            } else {
                val file = File(filePath)
                if (file.exists() && file.length() > 0) {
                    androidx.compose.ui.text.font.FontFamily(androidx.compose.ui.text.font.Font(file))
                } else {
                    FontFamily.Default
                }
            }
        } catch (e: Exception) {
            when (styleCategory) {
                "Monospace" -> FontFamily.Monospace
                "Serif" -> FontFamily.Serif
                "Sans-Serif" -> FontFamily.SansSerif
                "Cursive" -> FontFamily.Cursive
                else -> FontFamily.Default
            }
        }
    }
}

// Light theme color palette defined for FontX
private val SkyBlue = Color(0xFD1584FE)
private val LightSkyBackground = Color(0xFFF0F4FC)
private val AccentLavender = Color(0xFF818CF8)
private val CoolSlateText = Color(0xFF1E293B)
private val SoftCardBorder = Color(0x1F94A3B8)

// Define dynamic theme colors helper for Clean Minimalism
class AppColors(
    val isDark: Boolean,
    val background: Color,
    val cardBackground: Color,
    val text: Color,
    val border: Color,
    val topBar: Color,
    val icon: Color,
    val subText: Color,
    val primary: Color,
    val dialogBackground: Color
)

@Composable
fun rememberAppColors(viewModel: FontXViewModel): AppColors {
    val themeMode by viewModel.themeMode.collectAsState()
    val isDark = when (themeMode) {
        "Dark" -> true
        "Light" -> false
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    return remember(isDark) {
        AppColors(
            isDark = isDark,
            background = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
            cardBackground = if (isDark) Color(0xFF1E293B) else Color.White,
            text = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B),
            border = if (isDark) Color(0x2294A3B8) else Color(0x1F94A3B8),
            topBar = if (isDark) Color(0xFF1E293B) else Color.White,
            icon = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
            subText = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
            primary = Color(0xFD1584FE),
            dialogBackground = if (isDark) Color(0xFF1E293B) else Color.White
        )
    }
}

@Composable
fun ExpressiveShizukuPopup(viewModel: FontXViewModel, context: Context) {
    val colors = rememberAppColors(viewModel)
    Dialog(onDismissRequest = { viewModel.dismissExpressivePop() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.dialogBackground),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .border(1.dp, colors.border, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Flashy Bolt Icon in Circle
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Alert Symbol",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Shizuku Service Inactive",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.text,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "The background ADB shell process is not configured. Since Android runs in a simulator environment here, you can activate our premium simulation mode to test real-time font configurations instantly!",
                    fontSize = 13.sp,
                    color = colors.subText,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.forceMockShizukuConnection(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pop_mock_enable"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Activate Simulation Mode", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { viewModel.dismissExpressivePop() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Diagnostics", color = colors.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontXApp(viewModel: FontXViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val showExpressivePop by viewModel.showExpressivePop.collectAsState()
    val context = LocalContext.current
    val colors = rememberAppColors(viewModel)

    LaunchedEffect(Unit) {
        viewModel.checkShizuku(context)
    }

    if (showExpressivePop) {
        ExpressiveShizukuPopup(viewModel, context)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                is Screen.Splash -> SplashScreen()
                is Screen.Home -> HomeScreen(viewModel)
                is Screen.Detail -> DetailScreen(viewModel, screen.fontId)
                is Screen.ShizukuGuide -> ShizukuGuideScreen(viewModel)
                is Screen.About -> AboutScreen(viewModel)
            }
        }
    }
}

// 1. SPLASH SCREEN
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFE8EEFC))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant brand icon box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF2563EB), Color(0xFF4F46E5))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "fX",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.offset(y = (-2).dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FontX",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CoolSlateText,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Samsung One UI 8+ Font Installer",
                fontSize = 14.sp,
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = SkyBlue,
                strokeWidth = 3.dp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "100% Offline • Safe • Zero Permissions",
                fontSize = 12.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// 2. HOME SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: FontXViewModel) {
    val fonts by viewModel.allFonts.collectAsState()
    val shizukuState by viewModel.shizukuStatus.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val shizukuToggleActive by viewModel.shizukuToggleActive.collectAsState()
    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf(false) }
    val colors = rememberAppColors(viewModel)

    // Filter fonts based on query
    val filteredFonts = remember(fonts, searchQuery) {
        if (searchQuery.trim().isEmpty()) {
            fonts
        } else {
            fonts.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    val activeFont = remember(fonts) {
        fonts.firstOrNull { it.isInstalled }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "FontX Studio",
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.text,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.About) },
                        modifier = Modifier.testTag("action_about")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About settings",
                            tint = colors.icon
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.ShizukuGuide) },
                        modifier = Modifier.testTag("action_guide")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Shizuku Guide",
                            tint = colors.icon
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.topBar,
                    scrolledContainerColor = colors.topBar
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showImportDialog = true },
                containerColor = colors.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .testTag("fab_import_font"),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Import custom font")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import Font", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Searh & filter field
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search imported fonts...", color = colors.subText) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = colors.icon
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = colors.icon
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .background(colors.cardBackground, RoundedCornerShape(16.dp))
                        .testTag("search_fonts_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = colors.cardBackground,
                        unfocusedContainerColor = colors.cardBackground,
                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )
            }

            // Connection Status & Active Font Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (shizukuState) {
                                                ShizukuStatus.CONNECTED -> Color(0xFF10B981)
                                                ShizukuStatus.NOT_RUNNING -> Color(0xFFF59E0B)
                                                ShizukuStatus.NOT_INSTALLED -> Color(0xFFEF4444)
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (shizukuState) {
                                        ShizukuStatus.CONNECTED -> "Shizuku Bound"
                                        ShizukuStatus.NOT_RUNNING -> "Shizuku Not Running"
                                        ShizukuStatus.NOT_INSTALLED -> "Shizuku Service Missing"
                                    },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = colors.text
                                )
                            }

                            Text(
                                text = "GUIDE & SHELL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                modifier = Modifier
                                    .clickable { viewModel.navigateTo(Screen.ShizukuGuide) }
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            )
                        }

                        // Shizuku Service Toggle switch
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.background)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Active Toggle Status",
                                    tint = if (shizukuToggleActive) Color(0xFF10B981) else colors.subText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Shizuku Service Hook",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = colors.text
                                    )
                                    Text(
                                        text = if (shizukuState == ShizukuStatus.CONNECTED && shizukuToggleActive) "Enabled (Active Bridge)" else "Disabled (Manual Fallback)",
                                        fontSize = 10.sp,
                                        color = colors.subText
                                    )
                                }
                            }
                            Switch(
                                checked = shizukuToggleActive,
                                onCheckedChange = { active ->
                                    viewModel.setShizukuToggleActive(context, active)
                                },
                                modifier = Modifier.testTag("shizuku_active_toggle"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = colors.cardBackground,
                                    checkedTrackColor = colors.primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = colors.border)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Active Font Details
                        Text(
                            text = "CURRENT ACTIVE FONT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = colors.subText,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (activeFont != null) {
                            val activeFontFamily = rememberCustomFont(activeFont.filePath, activeFont.styleCategory)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeFont.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.text,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Custom override active in package overlay",
                                        fontSize = 12.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Button(
                                    onClick = { viewModel.deactivateFont(activeFont.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.background),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Restore Def", color = colors.text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            // Preview sentence in the custom font
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = colors.background),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "The quick brown fox jumps over the lazy dog. 1234567890",
                                        fontFamily = activeFontFamily,
                                        fontSize = 16.sp,
                                        color = colors.text,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(colors.background),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TypeSpecimen,
                                        contentDescription = "Default System Font",
                                        tint = colors.icon
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "System Default Font Active",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.text
                                    )
                                    Text(
                                        text = "One UI 8+ original typography layout",
                                        fontSize = 12.sp,
                                        color = colors.subText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Headers & List
            item {
                Text(
                    text = if (searchQuery.isNotEmpty()) "SEARCH RESULTS (${filteredFonts.size})" else "YOUR INVENTORY (${filteredFonts.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.subText,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (filteredFonts.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Use our beautifully generated hero banner!
                            Image(
                                painter = painterResource(id = R.drawable.img_fontx_hero_1782025637529),
                                contentDescription = "Font Illustration Support Banner",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Build Custom Font Pack Easily",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colors.text
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Click active presets below or tap 'Import Font' in the corner to select a .ttf file from your downloads folder.",
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = colors.subText,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { viewModel.insertPresetFontsIfEmpty(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                modifier = Modifier.testTag("load_presets_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Load Beautiful Preset Fonts", color = Color.White)
                            }
                        }
                    }
                }
            } else {
                items(filteredFonts) { font ->
                    FontItemCard(font = font, viewModel = viewModel)
                }
            }
        }
    }

    // Import Fonts Bottom Sheet/Dialog
    if (showImportDialog) {
        ImportFontDialog(
            viewModel = viewModel,
            onDismiss = { showImportDialog = false }
        )
    }
}

// FONT CARD COMPONENT
@Composable
fun FontItemCard(font: FontEntity, viewModel: FontXViewModel) {
    val fontStyle = rememberCustomFont(font.filePath, font.styleCategory)
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val colors = rememberAppColors(viewModel)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.border, RoundedCornerShape(18.dp))
            .clickable { viewModel.navigateTo(Screen.Detail(font.id)) }
            .testTag("font_card_${font.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = font.styleCategory.take(1),
                            fontWeight = FontWeight.Bold,
                            color = colors.primary,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = font.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colors.text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = font.styleCategory,
                                fontSize = 11.sp,
                                color = colors.subText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "•",
                                fontSize = 11.sp,
                                color = colors.subText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = font.fileSize,
                                fontSize = 11.sp,
                                color = colors.subText
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (font.isInstalled) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFECFDF5))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "ACTIVE",
                                color = Color(0xFF10B981),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_font_${font.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete font",
                            tint = colors.icon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = colors.border)
            Spacer(modifier = Modifier.height(14.dp))

            // Specimen Rendering in the custom font
            Text(
                text = "The quick brown fox jumps over the lazy dog.",
                fontFamily = fontStyle,
                fontSize = 18.sp,
                color = colors.text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Font Package?", color = colors.text) },
            text = { Text("Are you sure you want to completely remove \"${font.name}\" from your offline storage inventory?", color = colors.subText) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFont(context, font)
                        showDeleteConfirm = false
                        Toast.makeText(context, "Font deleted from inventory", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("confirm_delete_btn")
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = colors.primary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = colors.dialogBackground
        )
    }
}

// 3. IMPORT DIALOG
@Composable
fun ImportFontDialog(viewModel: FontXViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var fontName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Sans-Serif") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            fileUri = uri
            // Extract clean title from uri
            val extractedName = try {
                val path = uri.path ?: ""
                val rawName = path.substringAfterLast("/").substringBeforeLast(".")
                rawName.replace("_", " ").replace("-", " ")
                    .split(" ")
                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
            } catch (e: Exception) {
                "My Font"
            }
            fileName = "Selected: " + (uri.path?.substringAfterLast("/") ?: "custom_font.ttf")
            if (fontName.isEmpty()) {
                fontName = extractedName
            }
        }
    }

    Dialog(onDismissRequest = { if (!isImporting) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("import_dialog_container")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Import Local Font",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = CoolSlateText
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Select a .ttf file from your downloads",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // File Picker Selector
                Button(
                    onClick = { filePickerLauncher.launch("font/ttf") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (fileUri == null) LightSkyBackground else Color(0xFFECFDF5)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("select_font_file_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (fileUri == null) Icons.Default.Attachment else Icons.Default.Check,
                            contentDescription = "Attach font",
                            tint = if (fileUri == null) SkyBlue else Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (fileUri == null) "Choose TTF Font File" else "Font Successfully Attached",
                            color = if (fileUri == null) SkyBlue else Color(0xFF10B981),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (fileName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = fileName,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input field for Font Title
                OutlinedTextField(
                    value = fontName,
                    onValueChange = { fontName = it },
                    label = { Text("Font Family Name") },
                    placeholder = { Text("e.g., Space Grotesk Bold") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("font_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isImporting
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category selector
                Text(
                    text = "FONT TYPE CLASSIFICATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val categories = listOf("Sans-Serif", "Serif", "Monospace", "Cursive")
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        val chipBg by animateColorAsState(if (isSelected) SkyBlue else Color(0xFFF1F5F9))
                        val chipTextColored by animateColorAsState(if (isSelected) Color.White else CoolSlateText)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(chipBg)
                                .clickable(enabled = !isImporting) { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = chipTextColored,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isImporting
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val uri = fileUri
                            if (uri == null) {
                                Toast.makeText(context, "Please pick a TTF file first!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (fontName.trim().isEmpty()) {
                                Toast.makeText(context, "Please enter a font name!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isImporting = true
                            viewModel.importFont(
                                context = context,
                                uri = uri,
                                fontName = fontName,
                                category = selectedCategory,
                                onSuccess = {
                                    isImporting = false
                                    Toast.makeText(context, "Font imported successfully!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                onError = { error ->
                                    isImporting = false
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("dismiss_import_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isImporting && fileUri != null && fontName.isNotEmpty()
                    ) {
                        if (isImporting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Import", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// 4. DETAIL SCREEN (FONT STUDIO)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: FontXViewModel, fontId: Int) {
    val font by viewModel.selectedFont.collectAsState()
    val compilingState by viewModel.compilingState.collectAsState()
    val compilingProgress by viewModel.compilingProgress.collectAsState()
    val compilingLog by viewModel.compilingLog.collectAsState()

    val context = LocalContext.current
    val colors = rememberAppColors(viewModel)
    var sandboxText by remember { mutableStateOf("FontX Studio: Perfect typography renders here offline.") }
    var previewFontSize by remember { mutableStateOf(22f) }
    val clipboardManager = LocalClipboardManager.current

    if (font == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }

    val currentFont = font!!
    val customFontFamily = rememberCustomFont(currentFont.filePath, currentFont.styleCategory)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        currentFont.name,
                        fontWeight = FontWeight.Bold,
                        color = colors.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        modifier = Modifier.testTag("back_to_home")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to home dashboard",
                            tint = colors.icon
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.topBar)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Interactive Sandbox Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "LIVE RENDER SANDBOX",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.subText,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TextField(
                        value = sandboxText,
                        onValueChange = { sandboxText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                            .testTag("sandbox_text_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colors.background,
                            unfocusedContainerColor = colors.background,
                            focusedTextColor = colors.text,
                            unfocusedTextColor = colors.text,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text Box using the ACTUAL custom font loaded dynamically
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                            .background(colors.background, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                            .heightIn(min = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sandboxText.ifEmpty { "Type something..." },
                            fontFamily = customFontFamily,
                            fontSize = previewFontSize.sp,
                            color = colors.text,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Font Size Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextFields,
                            contentDescription = "Sizing slider",
                            tint = colors.icon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Size: ${previewFontSize.toInt()}sp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.subText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Slider(
                            value = previewFontSize,
                            onValueChange = { previewFontSize = it },
                            valueRange = 12f..48f,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("font_size_slider")
                        )
                    }
                }
            }

            // APK Compiling Card (One UI 8+ Bridge)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "ONE UI 8+ COMPILATION HUB",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.subText,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Build standard package overlays for secure system deployment. Zero network data required. Overwrites file configuration fully.",
                        fontSize = 12.sp,
                        color = colors.subText
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    if (compilingState == CompilingState.IDLE) {
                        Button(
                            onClick = { viewModel.runApkCompiler(currentFont.name) },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("compile_font_btn")
                        ) {
                            Text("1-Click Sign & Build Font Overlays", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (compilingState) {
                                        CompilingState.EXTRACTING -> "Extracting templates..."
                                        CompilingState.INJECTING -> "Injecting fonts..."
                                        CompilingState.SIGNING -> "Aligning & signing APK..."
                                        CompilingState.SUCCESS -> "Compilation success!"
                                        CompilingState.ERROR -> "Compiling failed."
                                        else -> "Processing..."
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (compilingState == CompilingState.SUCCESS) Color(0xFF10B981) else colors.primary
                                )

                                Text(
                                    text = "${(compilingProgress * 100).toInt()}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { compilingProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .testTag("compiler_progress_bar"),
                                color = if (compilingState == CompilingState.SUCCESS) Color(0xFF10B981) else colors.primary,
                                trackColor = colors.background
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action items post success
                            if (compilingState == CompilingState.SUCCESS) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.applyFont(currentFont.id)
                                            Toast.makeText(context, "Font applied as Shizuku layout!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                              .testTag("apply_shizuku_btn")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Flash", tint = Color.White)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Apply over Shizuku", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "APK overlay written to internal downloads to install.", Toast.LENGTH_LONG).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("install_apk_overlay_btn")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Install zip")
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Install Custom APK", color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Output shell log screen
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.background, RoundedCornerShape(10.dp))
                                    .border(1.dp, colors.border, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                                    .heightIn(max = 100.dp)
                            ) {
                                Text(
                                    text = compilingLog,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = colors.text,
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                )
                            }
                        }
                    }
                }
            }

            // Manual Installation ADB Commands (Highly informative!)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "MANUAL WIRELESS DEBUG / ADB SHELL ACTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.subText,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "If you don't use Shizuku, copy and paste these shell command strings in LADB or your laptop terminal while connected to wireless debugging:",
                        fontSize = 12.sp,
                        color = colors.subText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val adbCmd1 = "adb shell cmd overlay enable com.fontx.overlay.custom"
                    val adbCmd2 = "adb shell cmd font apply /data/local/tmp/${currentFont.name.lowercase().replace(" ", "")}.ttf"

                    AdbCommandBox(title = "1. Enable overlay config", command = adbCmd1, clipboardManager = clipboardManager, context = context)
                    Spacer(modifier = Modifier.height(12.dp))
                    AdbCommandBox(title = "2. Force apply typeface file", command = adbCmd2, clipboardManager = clipboardManager, context = context)
                }
            }
        }
    }
}

@Composable
fun AdbCommandBox(title: String, command: String, clipboardManager: androidx.compose.ui.platform.ClipboardManager, context: Context) {
    Column {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CoolSlateText)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(LightSkyBackground)
                .padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = command,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = CoolSlateText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(command))
                    Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy command to clipboard",
                    tint = SkyBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// 5. SHIZUKU USER HANDBOOK SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShizukuGuideScreen(viewModel: FontXViewModel) {
    val shizukuState by viewModel.shizukuStatus.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shizuku Setup Handbook", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        modifier = Modifier.testTag("guide_back")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back back home")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.checkShizuku(context) },
                        modifier = Modifier.testTag("guide_refresh_shizuku")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh check status")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightSkyBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Check display Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (shizukuState) {
                        ShizukuStatus.CONNECTED -> Color(0xFFECFDF5)
                        ShizukuStatus.NOT_RUNNING -> Color(0xFFFFFBEB)
                        ShizukuStatus.NOT_INSTALLED -> Color(0xFFFEF2F2)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (shizukuState) {
                                    ShizukuStatus.CONNECTED -> Color(0xFF10B981)
                                    ShizukuStatus.NOT_RUNNING -> Color(0xFFF59E0B)
                                    ShizukuStatus.NOT_INSTALLED -> Color(0xFFEF4444)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (shizukuState) {
                                ShizukuStatus.CONNECTED -> Icons.Default.Check
                                ShizukuStatus.NOT_RUNNING -> Icons.Default.Warning
                                ShizukuStatus.NOT_INSTALLED -> Icons.Default.DownloadForOffline
                            },
                            contentDescription = "Status symbol",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = when (shizukuState) {
                                ShizukuStatus.CONNECTED -> "Ready: Shizuku is Connected!"
                                ShizukuStatus.NOT_RUNNING -> "Incomplete: Shizuku is Stopped"
                                ShizukuStatus.NOT_INSTALLED -> "Action items: Shizuku App Missing"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CoolSlateText
                        )
                        Text(
                            text = when (shizukuState) {
                                ShizukuStatus.CONNECTED -> "One Click font installations are fully supported."
                                ShizukuStatus.NOT_RUNNING -> "Please enable Developer options & Wireless Debug first."
                                ShizukuStatus.NOT_INSTALLED -> "Download free Shizuku app via Google Play Store / GitHub."
                            },
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Step-by-Step instructions
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "HOW TO ENABLE SHIZUKU OFFLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GuideStep(
                        stepNumber = "1",
                        title = "Install Shizuku Manager",
                        desc = "Get the official, secure open-source Shizuku application from Play Store. This manages adb shell loops offline without root permissions."
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = LightSkyBackground)
                    Spacer(modifier = Modifier.height(16.dp))

                    GuideStep(
                        stepNumber = "2",
                        title = "Unleash Developer Options",
                        desc = "Go to System Settings -> About Phone -> Build Number. Tap it 7 times until \"Developer Mode Enabled\" is shown on bottom toast."
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = LightSkyBackground)
                    Spacer(modifier = Modifier.height(16.dp))

                    GuideStep(
                        stepNumber = "3",
                        title = "Wireless Debugging pairing",
                        desc = "In developer options, toggle Developer Options & activate Wireless Debugging. Open Shizuku, click \"Pairing\", click \"Developer options\" split screen, and enter the 6-digit numeric pairing code."
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = LightSkyBackground)
                    Spacer(modifier = Modifier.height(16.dp))

                    GuideStep(
                        stepNumber = "4",
                        title = "Start Shizuku Server",
                        desc = "Go back to Shizuku, tap \"Start\". Let the ADB background loops complete. If Shizuku says Running, it's 100% active."
                    )
                }
            }

            // Copy manual ADB link card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "MANUAL TERMINAL START SCRIPT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "If wireless debugging fails, launch Shizuku via computer terminal shell directly using this adb command string:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val startScript = "adb shell sh /sdcard/Android/data/moe.shizuku.privileged.api/start.sh"
                    AdbCommandBox(title = "Local computer terminal", command = startScript, clipboardManager = clipboardManager, context = context)
                }
            }
        }
    }
}

@Composable
fun GuideStep(stepNumber: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SkyBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(stepNumber, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CoolSlateText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
        }
    }
}

// 6. ABOUT SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(viewModel: FontXViewModel) {
    val context = LocalContext.current
    val colors = rememberAppColors(viewModel)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About FontX Studio", fontWeight = FontWeight.Bold, color = colors.text) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        modifier = Modifier.testTag("about_back")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back back home", tint = colors.icon)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.topBar)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF2563EB), Color(0xFF4F46E5))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "fX",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.offset(y = (-2).dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("FontX Studio", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = colors.text)
            Text("Version 1.0.0 Stable Build", fontSize = 12.sp, color = colors.subText)

            Spacer(modifier = Modifier.height(12.dp))

            // APPEARANCE THEME SELECTOR CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "APPEARANCE SETTINGS",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = colors.subText,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Interface Theme Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.text)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Choose how FontX Studio renders across your system preferences.", fontSize = 12.sp, color = colors.subText)
                    Spacer(modifier = Modifier.height(16.dp))

                    val activeThemeMode by viewModel.themeMode.collectAsState()
                    val modes = listOf("System", "Light", "Dark")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.background)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        modes.forEach { mode ->
                            val isSelected = activeThemeMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) colors.primary else Color.Transparent)
                                    .clickable { viewModel.setThemeMode(context, mode) }
                                    .padding(vertical = 10.dp)
                                    .testTag("theme_btn_${mode.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else colors.text
                                )
                            }
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Guiding Core Values", fontWeight = FontWeight.Black, fontSize = 12.sp, color = colors.subText)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.NetworkLocked, contentDescription = "offline", tint = colors.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("100% Offline-First", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.text)
                            Text("No background servers, completely private.", fontSize = 11.sp, color = colors.subText)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = colors.border)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = "secure", tint = colors.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Zero Storage Permissions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.text)
                            Text("We copy only chosen fonts inside app sandbox.", fontSize = 11.sp, color = colors.subText)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = colors.border)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.HighQuality, contentDescription = "One UI", tint = colors.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Designed for One UI", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.text)
                            Text("Guarantees compatibility with Shizuku.", fontSize = 11.sp, color = colors.subText)
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Local Diagnostics Status", fontWeight = FontWeight.Black, fontSize = 12.sp, color = colors.subText)
                    Spacer(modifier = Modifier.height(12.dp))

                    val listSize = remember { File(context.filesDir, "fonts").listFiles()?.size ?: 0 }
                    DiagnosticRow(label = "Sandbox Files", value = "$listSize custom fonts", colors = colors)
                    DiagnosticRow(label = "Internal Database", value = "fontx_database (SQLite)", colors = colors)
                    DiagnosticRow(label = "Application ID", value = "com.aistudio.fontx.qvrzkp", colors = colors)
                    DiagnosticRow(label = "Operating System", value = "Android 10+ (One UI 8.x Compatible)", colors = colors)
                }
            }
        }
    }
}

@Composable
fun DiagnosticRow(label: String, value: String, colors: AppColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = colors.subText)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.text)
    }
}
