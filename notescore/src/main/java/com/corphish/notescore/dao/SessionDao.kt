package com.corphish.notescore.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.corphish.notescore.models.Session
import com.corphish.notescore.models.User

@Dao
interface SessionDao {
    @Insert
    suspend fun createSession(session: Session)

    @Delete
    suspend fun removeSession(session: Session)

    @Query("SELECT * FROM session WHERE userId = :userId")
    suspend fun getSessionForUser(userId: Int): Session?

    @Query("SELECT * FROM session")
    suspend fun getAllSessions(): List<Session>

    @Query("DELETE FROM session")
    suspend fun removeAllSessions()
}