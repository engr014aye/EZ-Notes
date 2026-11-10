package com.fawadini.eznotes.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.fawadini.eznotes.data.model.Note

object ShareHelper {

    fun shareNoteAsText(context: Context, note: Note) {
        val textToShare = buildString {
            if (note.title.isNotBlank()) {
                append(note.title)
                append("\n\n")
            }
            if (note.isTodo) {
                note.todoItems.forEach { item ->
                    val status = if (item.isDone) "[x] " else "[ ] "
                    append(status)
                    append(item.text)
                    append("\n")
                }
            } else {
                append(note.content)
            }
        }.trim()

        if (textToShare.isBlank()) {
            Toast.makeText(context, "Nothing to share", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                if (note.title.isNotBlank()) {
                    putExtra(Intent.EXTRA_SUBJECT, note.title)
                }
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share note via")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share note", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to copy", Toast.LENGTH_SHORT).show()
        }
    }
}
