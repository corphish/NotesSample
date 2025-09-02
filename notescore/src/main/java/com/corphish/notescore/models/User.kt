package com.corphish.notescore.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)] // 👈 Make username unique
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val displayName: String,
    val passwordHash: String,  // Store the hashed password
    val salt: String           // Store the salt used for hashing
): Parcelable