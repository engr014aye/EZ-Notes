package com.fawadini.eznotes.data.repository

import com.fawadini.eznotes.data.local.converter.Converters
import com.fawadini.eznotes.data.local.dao.NoteDao
import com.fawadini.eznotes.data.local.entity.NoteEntity
import com.fawadini.eznotes.data.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(
    private val noteDao: NoteDao,
    private val converters: Converters = Converters()
) : NoteRepository {

    override fun getAllActiveNotes(): Flow<List<Note>> {
        return noteDao.getAllActiveNotes().map { list ->
            list.map { entity ->
                entity.toDomain(converters.toTodoItemList(entity.todoItemsJson))
            }
        }
    }

    override fun getArchivedNotes(): Flow<List<Note>> {
        return noteDao.getArchivedNotes().map { list ->
            list.map { entity ->
                entity.toDomain(converters.toTodoItemList(entity.todoItemsJson))
            }
        }
    }

    override fun getTrashNotes(): Flow<List<Note>> {
        return noteDao.getTrashNotes().map { list ->
            list.map { entity ->
                entity.toDomain(converters.toTodoItemList(entity.todoItemsJson))
            }
        }
    }

    override fun getNoteById(id: Long): Flow<Note?> {
        return noteDao.getNoteById(id).map { entity ->
            entity?.let {
                it.toDomain(converters.toTodoItemList(it.todoItemsJson))
            }
        }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        val sanitized = query.trim()
        return noteDao.searchNotes(sanitized).map { list ->
            list.map { entity ->
                entity.toDomain(converters.toTodoItemList(entity.todoItemsJson))
            }
        }
    }

    override suspend fun saveNote(note: Note): Result<Long> = withContext(Dispatchers.IO) {
        runCatching {
            val json = converters.fromTodoItemList(note.todoItems)
            val entity = NoteEntity.fromDomain(
                note = note.copy(updatedAt = System.currentTimeMillis()),
                todoItemsJson = json
            )
            noteDao.insertNote(entity)
        }
    }

    override suspend fun togglePin(id: Long, isPinned: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            noteDao.updatePinStatus(id, isPinned)
            Unit
        }
    }

    override suspend fun moveToTrash(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            noteDao.updateTrashStatus(id, true)
            Unit
        }
    }

    override suspend fun restoreFromTrash(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            noteDao.updateTrashStatus(id, false)
            Unit
        }
    }

    override suspend fun toggleArchive(id: Long, isArchived: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            noteDao.updateArchiveStatus(id, isArchived)
            Unit
        }
    }

    override suspend fun deletePermanently(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            noteDao.deleteNoteById(id)
            Unit
        }
    }

    override suspend fun emptyTrash(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            noteDao.emptyTrash()
            Unit
        }
    }

    override suspend fun duplicateNote(id: Long): Result<Long> = withContext(Dispatchers.IO) {
        runCatching {
            val original = noteDao.getNoteByIdSync(id)
                ?: throw IllegalArgumentException("Note with id $id not found")
            val copyEntity = original.copy(
                id = 0,
                title = if (original.title.isNotBlank()) "${original.title} (Copy)" else "Copy",
                isPinned = false,
                isArchived = false,
                isTrash = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            noteDao.insertNote(copyEntity)
        }
    }

    override fun getAppStats(): Flow<AppStats> {
        return combine(
            noteDao.getActiveCount(),
            noteDao.getArchivedCount(),
            noteDao.getTrashCount(),
            noteDao.getTodoCount()
        ) { active, archived, trash, todos ->
            AppStats(
                activeNotesCount = active,
                archivedNotesCount = archived,
                trashNotesCount = trash,
                todoListsCount = todos
            )
        }
    }
}
