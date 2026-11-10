package com.fawadini.eznotes.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fawadini.eznotes.EzNotesApp
import com.fawadini.eznotes.ui.screens.HomeScreen
import com.fawadini.eznotes.ui.screens.NoteEditScreen
import com.fawadini.eznotes.ui.screens.SettingsScreen
import com.fawadini.eznotes.ui.screens.TodoEditScreen
import com.fawadini.eznotes.ui.screens.TrashScreen
import com.fawadini.eznotes.ui.viewmodel.HomeViewModel
import com.fawadini.eznotes.ui.viewmodel.NoteEditViewModel
import com.fawadini.eznotes.ui.viewmodel.SettingsViewModel
import com.fawadini.eznotes.ui.viewmodel.TodoEditViewModel
import com.fawadini.eznotes.ui.viewmodel.TrashViewModel
import com.fawadini.eznotes.ui.viewmodel.ViewModelFactory

@Composable
fun EzNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current.applicationContext as EzNotesApp
    val repository = context.repository

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
        modifier = modifier
    ) {
        // Home Screen
        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = ViewModelFactory(repository)
            )
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToNoteEdit = { noteId ->
                    navController.navigate(Screen.NoteEdit.createRoute(noteId))
                },
                onNavigateToTodoEdit = { noteId ->
                    navController.navigate(Screen.TodoEdit.createRoute(noteId))
                },
                onNavigateToTrash = {
                    navController.navigate(Screen.Trash.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Note Edit Screen
        composable(
            route = Screen.NoteEdit.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            val noteEditViewModel: NoteEditViewModel = viewModel(
                key = "note_edit_$noteId",
                factory = ViewModelFactory(repository, noteId)
            )
            NoteEditScreen(
                viewModel = noteEditViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // To-Do Edit Screen
        composable(
            route = Screen.TodoEdit.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            val todoEditViewModel: TodoEditViewModel = viewModel(
                key = "todo_edit_$noteId",
                factory = ViewModelFactory(repository, noteId)
            )
            TodoEditScreen(
                viewModel = todoEditViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Trash Screen
        composable(route = Screen.Trash.route) {
            val trashViewModel: TrashViewModel = viewModel(
                factory = ViewModelFactory(repository)
            )
            TrashScreen(
                viewModel = trashViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Settings Screen
        composable(route = Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = ViewModelFactory(repository)
            )
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
