package com.corphish.notescore.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.corphish.notescore.dao.NoteDao
import com.corphish.notescore.dao.SessionDao
import com.corphish.notescore.dao.UserDao
import com.corphish.notescore.models.Note
import com.corphish.notescore.models.Session
import com.corphish.notescore.models.User

/**
 * Consolidated database for notes and users capabilities.
 */
@Database(entities = [User::class, Note::class, Session::class], version = 3, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getInstance(context: Context): NotesDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_database"
                ).fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}