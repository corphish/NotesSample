package com.corphish.notescore.api.functions

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.corphish.notescore.dao.SessionDao
import com.corphish.notescore.dao.UserDao
import com.corphish.notescore.models.Session
import com.corphish.notescore.models.User
import com.corphish.notescore.utils.PasswordHasher

/**
 * Provides user related functions.
 */
class UserFunctions(
    private val userDao: UserDao,
    private val sessionDao: SessionDao,
) {
    private val _tag = "UserFunctions"

    /**
     * Registers the user with the given details.
     * If a user already exists or new user could not be created, null will be returned.
     * Otherwise an User object will be returned with the given details.
     */
    suspend fun registerUser(
        username: String,
        displayName: String,
        password: String
    ): User? {
        val salt = PasswordHasher.generateSalt()
        val passwordHash = PasswordHasher.hashPassword(password, salt)

        val user = User(
            username = username,
            displayName = displayName,
            passwordHash = passwordHash,
            salt = salt
        )

        try {
            userDao.insert(user)
            return user
        } catch (e: SQLiteConstraintException) {
            Log.e(_tag, "Username already exists: ${user.username}", e)
            return null
        }
    }

    /**
     * Authenticates an user with given username and password.
     * Returns null if authentication is failed, user object otherwise
     * for the given credentials.
     */
    suspend fun authenticateUser(
        username: String,
        password: String
    ): User? {
        val user = userDao.getUserByUsername(username) ?: return null
        return if (PasswordHasher.verifyPassword(password, user.salt, user.passwordHash)) {
            user
        } else {
            null
        }
    }

    /**
     * Logs in the given user by creating a session of the same.
     * This also clears any previous session.
     */
    suspend fun loginUser(user: User) {
        val session = Session(
            userId = user.id
        )

        sessionDao.removeAllSessions()
        sessionDao.createSession(session)
    }

    /**
     * Logs out the current user if a session exists for the user.
     * Returns true if logout was successful.
     * Returns false if logout was not successful, possible caused by session not being found
     * for the user.
     */
    suspend fun logoutUser(user: User): Boolean {
        // Get the existing
        val session = sessionDao.getSessionForUser(user.id) ?: return false

        sessionDao.removeSession(session)
        return true
    }

    /**
     * Gets the currently logged in user. At a time only 1 user can be logged in.
     * This will be in effect if previously an user was logged in using the loginUser method.
     * If no user was logged in before, null will be returned.
     * If for some reason multiple users were logged in (due to implementation error), all
     * such sessions will be deleted and null will be returned (start fresh).
     * If a logged in user was found indeed, we will check when was the last time the user was logged
     * in, if it exceeds the TTL time period, user must be logged in again so null will be returned.
     *
     * Else the logged in user will be returned.
     */
    suspend fun getCurrentlyLoggedInUser(): User? {
        val allSessions = sessionDao.getAllSessions()

        // If there are multiple sessions found, that means there is something
        // wrong in the implementation, so better clear all the sessions and start fresh
        if (allSessions.size > 1) {
            sessionDao.removeAllSessions()
            return null
        }

        // If no session was found
        if (allSessions.isEmpty()) {
            return null
        }

        val session = allSessions[0]
        val loginDuration = System.currentTimeMillis() - session.loginTime

        if (loginDuration > SESSION_TTL) {
            return null
        }

        return userDao.getUserById(session.userId)
    }

    companion object {
        // TTL for each session
        const val SESSION_TTL = 7 * 24 * 60 * 60 * 1000L
    }
}