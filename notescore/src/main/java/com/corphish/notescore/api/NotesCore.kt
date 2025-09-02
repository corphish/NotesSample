package com.corphish.notescore.api

import android.content.Context
import com.corphish.notescore.api.functions.NoteFunctions
import com.corphish.notescore.api.functions.UserFunctions
import com.corphish.notescore.db.NotesDatabase
import com.corphish.notescore.models.User

/**
 * Object that provides all the functions to its clients.
 */
object NotesCore {
    private var database: NotesDatabase? = null

    /**
     * Sets up the room database for use.
     * Must be called from the onCreate of the Application class.
     */
    fun init(context: Context) {
        if (database == null) {
            database = NotesDatabase.getInstance(context)
        }
    }

    fun userFunctions() = UserFunctions(checkNotNull(database) {
        "Please call NotesCore#init method first"
    }.userDao())

    fun noteFunctions(user: User) = NoteFunctions(
        user = user,
        noteDao = checkNotNull(database) {
            "Please call NotesCore#init method first"
        }.noteDao()
    )
}