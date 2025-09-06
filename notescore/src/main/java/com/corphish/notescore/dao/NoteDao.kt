package com.corphish.notescore.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.corphish.notescore.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY creationTime DESC")
    fun getAllNotesCreatedByUser(userId: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE userId = :userId AND creationTime <= :createdBefore ORDER BY creationTime LIMIT :count")
    suspend fun getNotesCreatedByUserCreatedBefore(userId: Int, createdBefore: Long, count: Int = 10): List<Note>

    @Delete
    suspend fun delete(note: Note)
}