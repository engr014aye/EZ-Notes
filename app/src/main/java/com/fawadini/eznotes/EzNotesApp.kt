package com.fawadini.eznotes

import android.app.Application
import com.fawadini.eznotes.data.local.database.EzNotesDatabase
import com.fawadini.eznotes.data.repository.NoteRepository
import com.fawadini.eznotes.data.repository.NoteRepositoryImpl

class EzNotesApp : Application() {

    val database: EzNotesDatabase by lazy {
        EzNotesDatabase.getDatabase(this)
    }

    val repository: NoteRepository by lazy {
        NoteRepositoryImpl(database.noteDao())
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: EzNotesApp
            private set
    }
}
