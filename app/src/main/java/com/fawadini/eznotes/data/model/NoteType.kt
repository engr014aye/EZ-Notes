package com.fawadini.eznotes.data.model

enum class NoteType {
    NOTE,
    TODO;

    companion object {
        fun fromString(value: String): NoteType {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                NOTE
            }
        }
    }
}
