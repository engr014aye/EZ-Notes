package com.fawadini.eznotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fawadini.eznotes.data.repository.NoteRepository

class ViewModelFactory(
    private val repository: NoteRepository,
    private val noteId: Long = -1L
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NoteEditViewModel::class.java) -> {
                NoteEditViewModel(repository, noteId) as T
            }
            modelClass.isAssignableFrom(TodoEditViewModel::class.java) -> {
                TodoEditViewModel(repository, noteId) as T
            }
            modelClass.isAssignableFrom(TrashViewModel::class.java) -> {
                TrashViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
