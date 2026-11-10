package com.fawadini.eznotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.data.model.NoteColor
import com.fawadini.eznotes.util.DateFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var showMenu by remember { mutableStateOf(false) }

    // Resolve card background based on selected note color
    val cardBg = if (note.color == NoteColor.DEFAULT) {
        if (isDark) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface
    } else {
        if (isDark) note.color.darkBackground else note.color.lightBackground
    }

    val contentColor = if (isDark) Color.White else Color(0xFF1E293B)
    val secondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(
            1.dp,
            if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Action (Pin / Menu)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (note.title.isNotBlank()) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = if (note.isTodo) "Untitled Checklist" else "Untitled Note",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal,
                            color = secondaryColor.copy(alpha = 0.8f)
                        ),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isPinned) {
                        IconButton(
                            onClick = onPinClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PushPin,
                                contentDescription = "Unpin note",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Options",
                                tint = secondaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (note.isPinned) "Unpin" else "Pin") },
                                onClick = {
                                    showMenu = false
                                    onPinClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Duplicate") },
                                onClick = {
                                    showMenu = false
                                    onDuplicateClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Move to Trash") },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body Content: Depends on whether it's a To-Do or plain text Note
            if (note.isTodo) {
                // Checklist Preview
                if (note.todoItems.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        note.todoItems.take(4).forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = if (item.isDone) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = if (item.isDone) Color(0xFF10B981) else secondaryColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (item.isDone) secondaryColor else contentColor,
                                        textDecoration = if (item.isDone) TextDecoration.LineThrough else null
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (note.todoItems.size > 4) {
                            Text(
                                text = "+${note.todoItems.size - 4} more items",
                                style = MaterialTheme.typography.labelMedium.copy(color = secondaryColor),
                                modifier = Modifier.padding(start = 20.dp, top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LinearProgressIndicator(
                            progress = { note.completionRatio },
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF10B981),
                            trackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${note.completedTodoCount}/${note.totalTodoCount}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = secondaryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                } else {
                    Text(
                        text = "Empty checklist",
                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryColor),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            } else {
                // Plain Text Note Preview
                if (note.content.isNotBlank()) {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium.copy(color = contentColor),
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "Empty note",
                        style = MaterialTheme.typography.bodyMedium.copy(color = secondaryColor),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Date & Type Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateFormatter.formatNoteDate(note.updatedAt),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = secondaryColor,
                        fontSize = 11.sp
                    )
                )

                Surface(
                    color = if (note.isTodo) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (note.isTodo) "To-Do" else "Note",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (note.isTodo) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
