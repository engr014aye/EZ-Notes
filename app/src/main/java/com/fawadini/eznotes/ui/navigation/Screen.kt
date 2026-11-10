package com.fawadini.eznotes.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object NoteEdit : Screen("note_edit/{noteId}") {
        fun createRoute(noteId: Long = -1L): String = "note_edit/$noteId"
    }
    data object TodoEdit : Screen("todo_edit/{noteId}") {
        fun createRoute(noteId: Long = -1L): String = "todo_edit/$noteId"
    }
    data object Trash : Screen("trash")
    data object Settings : Screen("settings")
}
