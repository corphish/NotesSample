package com.corphish.notescore.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "session",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Session(
    @PrimaryKey(autoGenerate = true) val sessionId: Int = 0,

    // Currently logged in user
    val userId: Int,

    val loginTime: Long = System.currentTimeMillis()
)
