package com.corphish.notescore.api.functions

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.corphish.notescore.dao.UserDao
import com.corphish.notescore.models.User
import com.corphish.notescore.utils.PasswordHasher

/**
 * Provides user related functions.
 */
class UserFunctions(private val userDao: UserDao) {
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
}