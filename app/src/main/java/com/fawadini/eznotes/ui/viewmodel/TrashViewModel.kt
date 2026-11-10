package com.fawadini.eznotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TrashUiState(
    val trashNotes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null
)

class TrashViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    val uiState: StateFlow<TrashUiState> = combine(
        repository.getTrashNotes(),
        _message
    ) { notes, msg ->
        TrashUiState(
            trashNotes = notes,
            isLoading = false,
            message = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrashUiState(isLoading = true)
    )

    fun restoreNote(note: Note) {
        viewModelScope.launch {
            repository.restoreFromTrash(note.id)
            _message.value = "Restored: ${note.title.ifBlank { "Note" }}"
        }
    }

    fun deletePermanently(note: Note) {
        viewModelScope.launch {
            repository.deletePermanently(note.id)
            _message.value = "Deleted permanently"
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
            _message.value = "Trash emptied"
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
