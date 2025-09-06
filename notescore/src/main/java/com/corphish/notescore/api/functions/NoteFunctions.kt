package com.corphish.notescore.api.functions

import com.corphish.notescore.dao.NoteDao
import com.corphish.notescore.models.Note
import com.corphish.notescore.models.User
import kotlinx.coroutines.flow.Flow

/**
 * Provides note related function for a given user.
 */
class NoteFunctions(
    private val user: User,
    private val noteDao: NoteDao
) {

    /**
     * Function to insert a note.
     */
    suspend fun insertNote(
        summary: String,
        details: String,
    ): Note {
        val note = Note(
            userId = user.id,
            summary = summary,
            details = details
        )

        noteDao.insert(note)
        return note
    }

    /**
     * Function to delete a given note.
     */
    suspend fun deleteNote(note: Note): Boolean {
        try {
            noteDao.delete(note)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Function to update an existing note.
     * Pass the old note and new summary and details.
     */
    suspend fun updateNote(oldNote: Note, newSummary: String, newDetails: String): Note {
        val note = Note(
            id = oldNote.id,
            userId = oldNote.userId,
            summary = newSummary,
            details = newDetails
        )

        noteDao.updateNote(note)
        return note
    }

    /**
     * Returns all the notes created by the user.
     */
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotesCreatedByUser(userId = user.id)

    /**
     * Returns `count` number of notes created by the user on or before `createdBefore`
     */
    suspend fun getNotesCreatedBefore(
        createdBefore: Long = System.currentTimeMillis(),
        count: Int = 10
    ) = noteDao.getNotesCreatedByUserCreatedBefore(
        userId = user.id,
        createdBefore = createdBefore,
        count = count
    )
}