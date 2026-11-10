package com.fawadini.eznotes.data.model

import java.util.UUID

/**
 * Model representing an individual checklist item.
 */
data class TodoItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val isDone: Boolean = false
)
