package com.fawadini.eznotes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fawadini.eznotes.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE is_trash = 0 AND is_archived = 0 ORDER BY is_pinned DESC, updated_at DESC")
    fun getAllActiveNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_trash = 0 AND is_archived = 1 ORDER BY updated_at DESC")
    fun getArchivedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_trash = 1 ORDER BY updated_at DESC")
    fun getTrashNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteByIdSync(id: Long): NoteEntity?

    @Query("""
        SELECT * FROM notes 
        WHERE is_trash = 0 AND is_archived = 0 
          AND (title LIKE '%' || :query || '%' 
               OR content LIKE '%' || :query || '%' 
               OR todo_items_json LIKE '%' || :query || '%')
        ORDER BY is_pinned DESC, updated_at DESC
    """)
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity): Int

    @Delete
    suspend fun deleteNote(note: NoteEntity): Int

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long): Int

    @Query("UPDATE notes SET is_pinned = :isPinned, updated_at = :updatedAt WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE notes SET is_archived = :isArchived, is_pinned = 0, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateArchiveStatus(id: Long, isArchived: Boolean, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE notes SET is_trash = :isTrash, is_pinned = 0, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateTrashStatus(id: Long, isTrash: Boolean, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("DELETE FROM notes WHERE is_trash = 1")
    suspend fun emptyTrash(): Int

    @Query("SELECT COUNT(*) FROM notes WHERE is_trash = 0 AND is_archived = 0")
    fun getActiveCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE is_trash = 0 AND is_archived = 1")
    fun getArchivedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE is_trash = 1")
    fun getTrashCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE is_trash = 0 AND is_archived = 0 AND type = 'TODO'")
    fun getTodoCount(): Flow<Int>
}
