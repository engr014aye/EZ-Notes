package com.fawadini.eznotes.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.ui.components.ConfirmDialog
import com.fawadini.eznotes.ui.components.EmptyStateView
import com.fawadini.eznotes.ui.viewmodel.TrashViewModel
import com.fawadini.eznotes.util.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: TrashViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEmptyTrashDialog by remember { mutableStateOf(false) }
    var noteToDeletePermanently by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(uiState.message) {
        val msg = uiState.message
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Trash", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.trashNotes.isNotEmpty()) {
                        TextButton(
                            onClick = { showEmptyTrashDialog = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Empty Trash", fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.trashNotes.isEmpty()) {
            EmptyStateView(
                title = "Trash is empty",
                subtitle = "Deleted notes and checklists will appear here.",
                icon = Icons.Outlined.DeleteSweep,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.trashNotes, key = { it.id }) { note ->
                    TrashNoteCard(
                        note = note,
                        onRestore = { viewModel.restoreNote(note) },
                        onDeletePermanently = { noteToDeletePermanently = note }
                    )
                }
            }
        }

        // Empty Trash Confirmation Dialog
        if (showEmptyTrashDialog) {
            ConfirmDialog(
                title = "Empty Trash?",
                message = "All ${uiState.trashNotes.size} items in the trash will be permanently erased. This cannot be undone.",
                confirmButtonText = "Empty All",
                onConfirm = {
                    showEmptyTrashDialog = false
                    viewModel.emptyTrash()
                },
                onDismiss = { showEmptyTrashDialog = false }
            )
        }

        // Single Item Permanent Delete Dialog
        noteToDeletePermanently?.let { note ->
            ConfirmDialog(
                title = "Delete Permanently?",
                message = "Are you sure you want to permanently erase \"${note.title.ifBlank { "Untitled" }}\"? This action cannot be undone.",
                confirmButtonText = "Delete Forever",
                onConfirm = {
                    noteToDeletePermanently = null
                    viewModel.deletePermanently(note)
                },
                onDismiss = { noteToDeletePermanently = null }
            )
        }
    }
}

@Composable
private fun TrashNoteCard(
    note: Note,
    onRestore: () -> Unit,
    onDeletePermanently: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color(0xFF1E293B)
    val secondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E293B) else Color.White
        ),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title.ifBlank { if (note.isTodo) "Untitled Checklist" else "Untitled Note" },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.previewText,
                style = MaterialTheme.typography.bodyMedium.copy(color = secondaryColor),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Deleted: ${DateFormatter.formatNoteDate(note.updatedAt)}",
                style = MaterialTheme.typography.labelMedium.copy(color = secondaryColor, fontSize = 11.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDeletePermanently,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Erase")
                }

                Spacer(modifier = Modifier.width(10.dp))

                FilledTonalButton(
                    onClick = onRestore,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restore")
                }
            }
        }
    }
}
