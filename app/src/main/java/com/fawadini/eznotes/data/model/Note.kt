package com.fawadini.eznotes.data.model

/**
 * Domain model representing a Note or To-Do Checklist.
 */
data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val type: NoteType = NoteType.NOTE,
    val todoItems: List<TodoItem> = emptyList(),
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isTrash: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isTodo: Boolean
        get() = type == NoteType.TODO

    val completedTodoCount: Int
        get() = todoItems.count { it.isDone }

    val totalTodoCount: Int
        get() = todoItems.size

    val completionRatio: Float
        get() = if (totalTodoCount > 0) completedTodoCount.toFloat() / totalTodoCount else 0f

    val wordCount: Int
        get() {
            return if (isTodo) {
                todoItems.sumOf { item ->
                    item.text.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
                }
            } else {
                content.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
            }
        }

    val charCount: Int
        get() {
            return if (isTodo) {
                todoItems.sumOf { it.text.length }
            } else {
                content.length
            }
        }

    val previewText: String
        get() {
            return if (isTodo) {
                if (todoItems.isEmpty()) "Empty checklist"
                else todoItems.take(4).joinToString("\n") { (if (it.isDone) "✓ " else "○ ") + it.text }
            } else {
                content.trim()
            }
        }

    val isBlank: Boolean
        get() {
            return if (isTodo) {
                title.isBlank() && todoItems.none { it.text.isNotBlank() }
            } else {
                title.isBlank() && content.isBlank()
            }
        }
}
