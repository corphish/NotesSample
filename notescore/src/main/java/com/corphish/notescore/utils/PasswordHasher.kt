package com.corphish.notescore.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordHasher {

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val saltedPassword = salt + password
        val hash = digest.digest(saltedPassword.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hash)
    }

    fun verifyPassword(inputPassword: String, storedSalt: String, storedHash: String): Boolean {
        val inputHash = hashPassword(inputPassword, storedSalt)
        return inputHash == storedHash
    }
}