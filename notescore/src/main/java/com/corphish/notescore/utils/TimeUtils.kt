package com.corphish.notescore.utils

object TimeUtils {
    /**
     * Use this function to get the time difference between now and passed time millis.
     */
    fun getTimeAgo(timeMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timeMillis

        if (diff < 0) return "in the future"

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            seconds < 60 -> "just now"
            minutes == 1L -> "a minute ago"
            minutes < 60 -> "$minutes minutes ago"
            hours == 1L -> "an hour ago"
            hours < 24 -> "$hours hours ago"
            days == 1L -> "yesterday"
            days < 7 -> "$days days ago"
            weeks == 1L -> "last week"
            weeks < 5 -> "$weeks weeks ago"
            months == 1L -> "last month"
            months < 12 -> "$months months ago"
            years == 1L -> "last year"
            else -> "$years years ago"
        }
    }

}