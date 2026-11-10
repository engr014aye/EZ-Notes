package com.fawadini.eznotes.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.data.model.NoteColor
import com.fawadini.eznotes.data.model.NoteType
import com.fawadini.eznotes.ui.components.ColorSelector
import com.fawadini.eznotes.ui.components.ConfirmDialog
import com.fawadini.eznotes.ui.viewmodel.TodoEditViewModel
import com.fawadini.eznotes.util.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditScreen(
    viewModel: TodoEditViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    var showColorPicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    // Intercept back button to safely auto-save
    BackHandler {
        viewModel.saveTodoNote(onFinished = onNavigateBack)
    }

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
                    IconButton(onClick = { viewModel.saveTodoNote(onFinished = onNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back and save",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    // Color Palette
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
                                type = NoteType.TODO,
                                todoItems = uiState.items
                            )
                            ShareHelper.shareNoteAsText(context, currentNote)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share checklist",
                            tint = textColor
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = textColor
                            )
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Clear completed items") },
                                onClick = {
                                    showMoreMenu = false
                                    viewModel.clearCompletedItems()
                                }
                            )
                            if (!uiState.isNewNote) {
                                DropdownMenuItem(
                                    text = { Text("Move to Trash", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMoreMenu = false
                                        showDeleteConfirm = true
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = screenBg)
            )
        },
        bottomBar = {
            // Task Adder Input Bar
            Surface(
                color = screenBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.currentInput,
                        onValueChange = { viewModel.onInputChange(it) },
                        placeholder = { Text("Add a task…", color = placeholderColor) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { viewModel.addTodoItem() }),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                            unfocusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = { viewModel.addTodoItem() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFF10B981)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add task",
                            tint = Color.White
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

                // Title Input
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
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
                                    text = "Checklist Title",
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
                }

                // Progress Banner (if items exist)
                if (uiState.totalCount > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${uiState.completedCount} of ${uiState.totalCount} completed",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor
                                    )
                                )
                                Text(
                                    text = "${(uiState.progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { uiState.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF10B981),
                                trackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                            )
                        }
                    }
                }

                // Tasks List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(uiState.items, key = { it.id }) { item ->
                        val checkTint by animateColorAsState(
                            if (item.isDone) Color(0xFF10B981) else placeholderColor,
                            label = "checkColor"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleItemDone(item.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.isDone) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                    contentDescription = if (item.isDone) "Mark not done" else "Mark done",
                                    tint = checkTint
                                )
                            }

                            BasicTextField(
                                value = item.text,
                                onValueChange = { viewModel.updateItemText(item.id, it) },
                                textStyle = TextStyle(
                                    color = if (item.isDone) placeholderColor else textColor,
                                    fontSize = 16.sp,
                                    textDecoration = if (item.isDone) TextDecoration.LineThrough else null
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )

                            IconButton(
                                onClick = { viewModel.removeItem(item.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Remove task",
                                    tint = placeholderColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirm) {
            ConfirmDialog(
                title = "Move to Trash?",
                message = "Are you sure you want to move this checklist to the trash?",
                confirmButtonText = "Move to Trash",
                onConfirm = {
                    showDeleteConfirm = false
                    viewModel.deleteTodoNote(onFinished = onNavigateBack)
                },
                onDismiss = { showDeleteConfirm = false }
            )
        }
    }
}
