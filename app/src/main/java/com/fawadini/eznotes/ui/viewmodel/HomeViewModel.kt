package com.fawadini.eznotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fawadini.eznotes.data.model.Note
import com.fawadini.eznotes.data.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FilterTab {
    ALL,
    NOTES,
    TODOS,
    PINNED
}

data class HomeUiState(
    val notes: List<Note> = emptyList(),
    val pinnedNotes: List<Note> = emptyList(),
    val otherNotes: List<Note> = emptyList(),
    val filterTab: FilterTab = FilterTab.ALL,
    val searchQuery: String = "",
    val isGridLayout: Boolean = true,
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterTab = MutableStateFlow(FilterTab.ALL)
    val filterTab = _filterTab.asStateFlow()

    private val _isGridLayout = MutableStateFlow(true)
    val isGridLayout = _isGridLayout.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    private var lastDeletedNoteId: Long? = null

    // Debounced search query flow
    private val debouncedSearch = _searchQuery
        .debounce(200)
        .distinctUntilChanged()

    // Notes flow responding to search query changes
    private val notesSourceFlow = debouncedSearch.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.getAllActiveNotes()
        } else {
            repository.searchNotes(query)
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        notesSourceFlow,
        _filterTab,
        _searchQuery,
        _isGridLayout,
        _snackbarMessage
    ) { rawNotes, tab, query, isGrid, snackbar ->
        val filtered = when (tab) {
            FilterTab.ALL -> rawNotes
            FilterTab.NOTES -> rawNotes.filter { !it.isTodo }
            FilterTab.TODOS -> rawNotes.filter { it.isTodo }
            FilterTab.PINNED -> rawNotes.filter { it.isPinned }
        }

        val pinned = filtered.filter { it.isPinned }
        val others = filtered.filter { !it.isPinned }

        HomeUiState(
            notes = filtered,
            pinnedNotes = pinned,
            otherNotes = others,
            filterTab = tab,
            searchQuery = query,
            isGridLayout = isGrid,
            isLoading = false,
            snackbarMessage = snackbar
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterTabSelected(tab: FilterTab) {
        _filterTab.value = tab
    }

    fun toggleLayout() {
        _isGridLayout.update { !it }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            repository.togglePin(note.id, !note.isPinned)
        }
    }

    fun moveToTrash(note: Note) {
        viewModelScope.launch {
            lastDeletedNoteId = note.id
            repository.moveToTrash(note.id)
            _snackbarMessage.value = "Note moved to trash"
        }
    }

    fun undoDelete() {
        val idToRestore = lastDeletedNoteId ?: return
        viewModelScope.launch {
            repository.restoreFromTrash(idToRestore)
            lastDeletedNoteId = null
            _snackbarMessage.value = "Note restored"
        }
    }

    fun duplicateNote(note: Note) {
        viewModelScope.launch {
            repository.duplicateNote(note.id)
            _snackbarMessage.value = "Note duplicated"
        }
    }

    fun toggleArchive(note: Note) {
        viewModelScope.launch {
            repository.toggleArchive(note.id, !note.isArchived)
            _snackbarMessage.value = if (!note.isArchived) "Archived" else "Unarchived"
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
