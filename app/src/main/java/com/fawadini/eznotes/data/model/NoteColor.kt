package com.fawadini.eznotes.data.model

import androidx.compose.ui.graphics.Color

/**
 * Modern pastel/card color options for notes.
 */
enum class NoteColor(
    val id: Int,
    val title: String,
    val lightBackground: Color,
    val darkBackground: Color,
    val accentColor: Color
) {
    DEFAULT(
        id = 0,
        title = "Default",
        lightBackground = Color(0xFFFFFFFF),
        darkBackground = Color(0xFF1E293B), // Slate 800
        accentColor = Color(0xFF2563EB)     // Blue 600
    ),
    CORAL(
        id = 1,
        title = "Coral",
        lightBackground = Color(0xFFFFEDEB),
        darkBackground = Color(0xFF451A1A),
        accentColor = Color(0xFFEF4444)
    ),
    AMBER(
        id = 2,
        title = "Amber",
        lightBackground = Color(0xFFFEF3C7),
        darkBackground = Color(0xFF452E0B),
        accentColor = Color(0xFFF59E0B)
    ),
    EMERALD(
        id = 3,
        title = "Emerald",
        lightBackground = Color(0xFFD1FAE5),
        darkBackground = Color(0xFF064E3B),
        accentColor = Color(0xFF10B981)
    ),
    SKY(
        id = 4,
        title = "Sky",
        lightBackground = Color(0xFFE0F2FE),
        darkBackground = Color(0xFF0C4A6E),
        accentColor = Color(0xFF0284C7)
    ),
    INDIGO(
        id = 5,
        title = "Indigo",
        lightBackground = Color(0xFFEEF2FF),
        darkBackground = Color(0xFF312E81),
        accentColor = Color(0xFF6366F1)
    ),
    ROSE(
        id = 6,
        title = "Rose",
        lightBackground = Color(0xFFFFE4E6),
        darkBackground = Color(0xFF4C0519),
        accentColor = Color(0xFFF43F5E)
    ),
    LAVENDER(
        id = 7,
        title = "Lavender",
        lightBackground = Color(0xFFF3E8FF),
        darkBackground = Color(0xFF3B0764),
        accentColor = Color(0xFF9333EA)
    );

    companion object {
        fun fromId(id: Int): NoteColor {
            return entries.firstOrNull { it.id == id } ?: DEFAULT
        }
    }
}
