package com.fawadini.eznotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.data.model.NoteColor
import com.fawadini.eznotes.data.model.NoteType
import com.fawadini.eznotes.data.model.TodoItem
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
import java.util.UUID

data class TodoEditUiState(
    val noteId: Long = 0,
    val title: String = "",
    val items: List<TodoItem> = emptyList(),
    val currentInput: String = "",
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isNewNote: Boolean = true,
    val isLoading: Boolean = false
) {
    val totalCount: Int
        get() = items.size

    val completedCount: Int
        get() = items.count { it.isDone }

    val progress: Float
        get() = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    val isBlank: Boolean
        get() = title.trim().isBlank() && items.none { it.text.trim().isNotBlank() } && currentInput.trim().isBlank()
}

sealed interface TodoEditEvent {
    data object TodoSaved : TodoEditEvent
    data object TodoDeleted : TodoEditEvent
    data class ShowMessage(val message: String) : TodoEditEvent
}

class TodoEditViewModel(
    private val repository: NoteRepository,
    private val initialNoteId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TodoEditUiState(
            noteId = if (initialNoteId > 0) initialNoteId else 0,
            isNewNote = initialNoteId <= 0
        )
    )
    val uiState: StateFlow<TodoEditUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TodoEditEvent>()
    val events: SharedFlow<TodoEditEvent> = _events.asSharedFlow()

    init {
        if (initialNoteId > 0) {
            loadTodoNote(initialNoteId)
        }
    }

    private fun loadTodoNote(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val note = repository.getNoteById(id).firstOrNull()
            if (note != null) {
                _uiState.update {
                    it.copy(
                        noteId = note.id,
                        title = note.title,
                        items = note.todoItems,
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

    fun onInputChange(input: String) {
        _uiState.update { it.copy(currentInput = input) }
    }

    fun addTodoItem() {
        val text = _uiState.value.currentInput.trim()
        if (text.isBlank()) return

        val newItem = TodoItem(
            id = UUID.randomUUID().toString(),
            text = text,
            isDone = false
        )
        _uiState.update {
            it.copy(
                items = it.items + newItem,
                currentInput = ""
            )
        }
    }

    fun toggleItemDone(itemId: String) {
        _uiState.update { state ->
            val updated = state.items.map {
                if (it.id == itemId) it.copy(isDone = !it.isDone) else it
            }
            state.copy(items = updated)
        }
    }

    fun updateItemText(itemId: String, newText: String) {
        _uiState.update { state ->
            val updated = state.items.map {
                if (it.id == itemId) it.copy(text = newText) else it
            }
            state.copy(items = updated)
        }
    }

    fun removeItem(itemId: String) {
        _uiState.update { state ->
            state.copy(items = state.items.filterNot { it.id == itemId })
        }
    }

    fun clearCompletedItems() {
        _uiState.update { state ->
            state.copy(items = state.items.filterNot { it.isDone })
        }
    }

    fun onColorSelected(newColor: NoteColor) {
        _uiState.update { it.copy(color = newColor) }
    }

    fun togglePin() {
        _uiState.update { it.copy(isPinned = !it.isPinned) }
    }

    fun saveTodoNote(onFinished: () -> Unit = {}) {
        val currentState = _uiState.value

        // Check if there's pending text in the input box and add it before saving
        val pendingText = currentState.currentInput.trim()
        val allItems = if (pendingText.isNotBlank()) {
            currentState.items + TodoItem(text = pendingText, isDone = false)
        } else {
            currentState.items
        }.filter { it.text.trim().isNotBlank() }

        if (currentState.title.trim().isBlank() && allItems.isEmpty()) {
            onFinished()
            return
        }

        viewModelScope.launch {
            val note = Note(
                id = currentState.noteId,
                title = currentState.title.trim(),
                content = "",
                type = NoteType.TODO,
                todoItems = allItems,
                color = currentState.color,
                isPinned = currentState.isPinned,
                isArchived = currentState.isArchived,
                createdAt = currentState.createdAt,
                updatedAt = System.currentTimeMillis()
            )

            val result = repository.saveNote(note)
            result.onSuccess {
                _events.emit(TodoEditEvent.TodoSaved)
                onFinished()
            }.onFailure {
                _events.emit(TodoEditEvent.ShowMessage("Failed to save checklist"))
            }
        }
    }

    fun deleteTodoNote(onFinished: () -> Unit = {}) {
        val noteId = _uiState.value.noteId
        if (noteId > 0) {
            viewModelScope.launch {
                repository.moveToTrash(noteId)
                _events.emit(TodoEditEvent.TodoDeleted)
                onFinished()
            }
        } else {
            onFinished()
        }
    }
}
