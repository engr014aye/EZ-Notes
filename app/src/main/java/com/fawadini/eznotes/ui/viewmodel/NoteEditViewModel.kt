package com.fawadini.eznotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.data.model.NoteColor
import com.fawadini.eznotes.data.model.NoteType
import com.fawadini.eznotes.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteEditUiState(
    val noteId: Long = 0,
    val title: String = "",
    val content: String = "",
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isNewNote: Boolean = true,
    val isLoading: Boolean = false
) {
    val wordCount: Int
        get() = content.trim().split("\\s+".toRegex()).count { it.isNotBlank() }

    val charCount: Int
        get() = content.length

    val isBlank: Boolean
        get() = title.trim().isBlank() && content.trim().isBlank()
}

sealed interface NoteEditEvent {
    data object NoteSaved : NoteEditEvent
    data object NoteDeleted : NoteEditEvent
    data class ShowMessage(val message: String) : NoteEditEvent
}

class NoteEditViewModel(
    private val repository: NoteRepository,
    private val initialNoteId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NoteEditUiState(
            noteId = if (initialNoteId > 0) initialNoteId else 0,
            isNewNote = initialNoteId <= 0
        )
    )
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NoteEditEvent>()
    val events: SharedFlow<NoteEditEvent> = _events.asSharedFlow()

    init {
        if (initialNoteId > 0) {
            loadNote(initialNoteId)
        }
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val note = repository.getNoteById(id).firstOrNull()
            if (note != null) {
                _uiState.update {
                    it.copy(
                        noteId = note.id,
                        title = note.title,
                        content = note.content,
                        color = note.color,
                        isPinned = note.isPinned,
                        isArchived = note.isArchived,
                        createdAt = note.createdAt,
                        updatedAt = note.updatedAt,
                        isNewNote = false,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onContentChange(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }

    fun onColorSelected(newColor: NoteColor) {
        _uiState.update { it.copy(color = newColor) }
    }

    fun togglePin() {
        _uiState.update { it.copy(isPinned = !it.isPinned) }
    }

    fun saveNote(onFinished: () -> Unit = {}) {
        val currentState = _uiState.value

        // Prevent saving completely empty notes
        if (currentState.isBlank) {
            onFinished()
            return
        }

        viewModelScope.launch {
            val note = Note(
                id = currentState.noteId,
                title = currentState.title.trim(),
                content = currentState.content.trim(),
                type = NoteType.NOTE,
                color = currentState.color,
                isPinned = currentState.isPinned,
                isArchived = currentState.isArchived,
                createdAt = currentState.createdAt,
                updatedAt = System.currentTimeMillis()
            )

            val result = repository.saveNote(note)
            result.onSuccess {
                _events.emit(NoteEditEvent.NoteSaved)
                onFinished()
            }.onFailure {
                _events.emit(NoteEditEvent.ShowMessage("Failed to save note"))
            }
        }
    }

    fun deleteNote(onFinished: () -> Unit = {}) {
        val noteId = _uiState.value.noteId
        if (noteId > 0) {
            viewModelScope.launch {
                repository.moveToTrash(noteId)
                _events.emit(NoteEditEvent.NoteDeleted)
                onFinished()
            }
        } else {
            onFinished()
        }
    }
}
