package com.fawadini.eznotes.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.data.model.NoteColor
import com.fawadini.eznotes.data.model.NoteType
import com.fawadini.eznotes.data.model.TodoItem

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "type")
    val type: String = "NOTE",

    @ColumnInfo(name = "todo_items_json")
    val todoItemsJson: String = "[]",

    @ColumnInfo(name = "color_index")
    val colorIndex: Int = 0,

    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "is_trash")
    val isTrash: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(todoItems: List<TodoItem>): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            type = NoteType.fromString(type),
            todoItems = todoItems,
            color = NoteColor.fromId(colorIndex),
            isPinned = isPinned,
            isArchived = isArchived,
            isTrash = isTrash,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(note: Note, todoItemsJson: String): NoteEntity {
            return NoteEntity(
                id = note.id,
                title = note.title,
                content = note.content,
                type = note.type.name,
                todoItemsJson = todoItemsJson,
                colorIndex = note.color.id,
                isPinned = note.isPinned,
                isArchived = note.isArchived,
                isTrash = note.isTrash,
                createdAt = note.createdAt,
                updatedAt = note.updatedAt
            )
        }
    }
}
