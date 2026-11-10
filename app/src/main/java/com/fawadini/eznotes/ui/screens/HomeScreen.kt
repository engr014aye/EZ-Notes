package com.fawadini.eznotes.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.ui.components.EmptyStateView
import com.fawadini.eznotes.ui.components.NoteCard
import com.fawadini.eznotes.ui.viewmodel.FilterTab
import com.fawadini.eznotes.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToNoteEdit: (Long) -> Unit,
    onNavigateToTodoEdit: (Long) -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFabMenu by remember { mutableStateOf(false) }

    // Handle snackbar messages (e.g., Undo delete)
    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (msg != null) {
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = if (msg.contains("trash", ignoreCase = true)) "Undo" else null,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            }
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EZ Notes",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                actions = {
                    // Layout Toggle (Grid / List)
                    IconButton(onClick = { viewModel.toggleLayout() }) {
                        Icon(
                            imageVector = if (uiState.isGridLayout) Icons.Default.ViewAgenda else Icons.Default.GridView,
                            contentDescription = "Toggle layout",
                            tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    }

                    // Trash Screen Shortcut
                    IconButton(onClick = onNavigateToTrash) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Trash",
                            tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    }

                    // Settings Shortcut
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secondary FAB: New Checklist
                AnimatedVisibility(
                    visible = showFabMenu,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 3.dp
                        ) {
                            Text(
                                text = "New Checklist",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                onNavigateToTodoEdit(-1L)
                            },
                            containerColor = Color(0xFF10B981),
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Checklist, contentDescription = "New Checklist")
                        }
                    }
                }

                // Secondary FAB: New Note
                AnimatedVisibility(
                    visible = showFabMenu,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 3.dp
                        ) {
                            Text(
                                text = "New Note",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                onNavigateToNoteEdit(-1L)
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = "New Note")
                        }
                    }
                }

                // Main Toggle FAB
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = if (showFabMenu) Icons.Default.Clear else Icons.Default.Add,
                        contentDescription = "Create note or checklist"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = {
                        Text(
                            text = "Search notes & to-dos…",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8))
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF94A3B8)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        unfocusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Filter Chips Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    FilterTab.ALL to "All",
                    FilterTab.NOTES to "Notes",
                    FilterTab.TODOS to "To-Dos",
                    FilterTab.PINNED to "Pinned"
                )

                items(tabs) { (tab, label) ->
                    val isSelected = uiState.filterTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onFilterTabSelected(tab) },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                            labelColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        ),
                        border = null,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Content Area: Notes list / grid / empty state
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.notes.isEmpty()) {
                if (uiState.searchQuery.isNotBlank()) {
                    EmptyStateView(
                        title = "No results found",
                        subtitle = "No notes or checklists match \"${uiState.searchQuery}\"",
                        icon = Icons.Outlined.SearchOff
                    )
                } else {
                    EmptyStateView(
                        title = when (uiState.filterTab) {
                            FilterTab.NOTES -> "No notes yet"
                            FilterTab.TODOS -> "No checklists yet"
                            FilterTab.PINNED -> "No pinned notes"
                            FilterTab.ALL -> "Your notebook is clear"
                        },
                        subtitle = when (uiState.filterTab) {
                            FilterTab.NOTES -> "Tap + below to write down your thoughts and ideas."
                            FilterTab.TODOS -> "Tap + below to create a simple to-do list."
                            FilterTab.PINNED -> "Pin your favorite notes or tasks to keep them here."
                            FilterTab.ALL -> "Capture thoughts, ideas, or to-do lists easily with zero clutter."
                        }
                    )
                }
            } else {
                if (uiState.isGridLayout) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalItemSpacing = 10.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.notes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = {
                                    if (note.isTodo) onNavigateToTodoEdit(note.id)
                                    else onNavigateToNoteEdit(note.id)
                                },
                                onPinClick = { viewModel.togglePin(note) },
                                onDeleteClick = { viewModel.moveToTrash(note) },
                                onDuplicateClick = { viewModel.duplicateNote(note) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.notes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = {
                                    if (note.isTodo) onNavigateToTodoEdit(note.id)
                                    else onNavigateToNoteEdit(note.id)
                                },
                                onPinClick = { viewModel.togglePin(note) },
                                onDeleteClick = { viewModel.moveToTrash(note) },
                                onDuplicateClick = { viewModel.duplicateNote(note) }
                            )
                        }
                    }
                }
            }
        }
    }
}
