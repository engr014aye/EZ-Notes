package com.fawadini.eznotes.data.repository

import com.fawadini.eznotes.data.model.Note
import kotlinx.coroutines.flow.Flow

data class AppStats(
    val activeNotesCount: Int = 0,
    val archivedNotesCount: Int = 0,
    val trashNotesCount: Int = 0,
    val todoListsCount: Int = 0
)

interface NoteRepository {
    fun getAllActiveNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    fun getTrashNotes(): Flow<List<Note>>
    fun getNoteById(id: Long): Flow<Note?>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun saveNote(note: Note): Result<Long>
    suspend fun togglePin(id: Long, isPinned: Boolean): Result<Unit>
    suspend fun moveToTrash(id: Long): Result<Unit>
    suspend fun restoreFromTrash(id: Long): Result<Unit>
    suspend fun toggleArchive(id: Long, isArchived: Boolean): Result<Unit>
    suspend fun deletePermanently(id: Long): Result<Unit>
    suspend fun emptyTrash(): Result<Unit>
    suspend fun duplicateNote(id: Long): Result<Long>
    fun getAppStats(): Flow<AppStats>
}
