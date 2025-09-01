package com.corphish.notessample

import android.app.Application
import com.corphish.notescore.api.NotesCore

class NotesApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        NotesCore.init(this)
    }
}