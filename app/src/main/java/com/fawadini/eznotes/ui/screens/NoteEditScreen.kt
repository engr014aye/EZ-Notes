package com.fawadini.eznotes.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.data.model.NoteColor
import com.fawadini.eznotes.data.model.NoteType
import com.fawadini.eznotes.ui.components.ColorSelector
import com.fawadini.eznotes.ui.components.ConfirmDialog
import com.fawadini.eznotes.ui.viewmodel.NoteEditViewModel
import com.fawadini.eznotes.util.DateFormatter
import com.fawadini.eznotes.util.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    viewModel: NoteEditViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    var showColorPicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Intercept back button to safely auto-save
    BackHandler {
        viewModel.saveNote(onFinished = onNavigateBack)
    }

    // Determine screen background from selected note color
    val screenBg = if (uiState.color == NoteColor.DEFAULT) {
        MaterialTheme.colorScheme.background
    } else {
        if (isDark) uiState.color.darkBackground else uiState.color.lightBackground
    }

    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val placeholderColor = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)

    Scaffold(
        containerColor = screenBg,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { viewModel.saveNote(onFinished = onNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back and save",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    // Color Palette Toggle
                    IconButton(onClick = { showColorPicker = !showColorPicker }) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Color palette",
                            tint = if (showColorPicker) MaterialTheme.colorScheme.primary else textColor
                        )
                    }

                    // Pin Toggle
                    IconButton(onClick = { viewModel.togglePin() }) {
                        Icon(
                            imageVector = if (uiState.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Toggle Pin",
                            tint = if (uiState.isPinned) MaterialTheme.colorScheme.primary else textColor
                        )
                    }

                    // Share
                    IconButton(
                        onClick = {
                            val currentNote = Note(
                                id = uiState.noteId,
                                title = uiState.title,
                                content = uiState.content,
                                type = NoteType.NOTE
                            )
                            ShareHelper.shareNoteAsText(context, currentNote)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share note",
                            tint = textColor
                        )
                    }

                    // Delete Note
                    if (!uiState.isNewNote) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete note",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = screenBg
                )
            )
        },
        bottomBar = {
            Surface(
                color = screenBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.wordCount} words  •  ${uiState.charCount} characters",
                            style = MaterialTheme.typography.labelMedium.copy(color = placeholderColor)
                        )
                        Text(
                            text = DateFormatter.formatNoteDate(uiState.updatedAt),
                            style = MaterialTheme.typography.labelMedium.copy(color = placeholderColor)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Animated Color Picker
                AnimatedVisibility(visible = showColorPicker) {
                    ColorSelector(
                        selectedColor = uiState.color,
                        onColorSelected = {
                            viewModel.onColorSelected(it)
                            showColorPicker = false
                        }
                    )
                }

                // Editor Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Title input field
                    BasicTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.onTitleChange(it) },
                        textStyle = TextStyle(
                            color = textColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (uiState.title.isEmpty()) {
                                Text(
                                    text = "Title",
                                    style = TextStyle(
                                        color = placeholderColor,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content input field
                    BasicTextField(
                        value = uiState.content,
                        onValueChange = { viewModel.onContentChange(it) },
                        textStyle = TextStyle(
                            color = textColor,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        decorationBox = { innerTextField ->
                            if (uiState.content.isEmpty()) {
                                Text(
                                    text = "Start typing your note here…",
                                    style = TextStyle(
                                        color = placeholderColor,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirm) {
            ConfirmDialog(
                title = "Move to Trash?",
                message = "Are you sure you want to move this note to the trash?",
                confirmButtonText = "Move to Trash",
                onConfirm = {
                    showDeleteConfirm = false
                    viewModel.deleteNote(onFinished = onNavigateBack)
                },
                onDismiss = { showDeleteConfirm = false }
            )
        }
    }
}
