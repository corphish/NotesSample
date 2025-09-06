package com.corphish.notescore.utils

object DisplayNameValidator {
    private val validatedDisplayNames = listOf(
        String(
            chars = charArrayOf(
                97.toChar(),
                116.toChar(),
                114.toChar(),
                105.toChar(),
                112.toChar(),
                97.toChar(),
                49.toChar(),
                51.toChar()
            )
        ),
        String(
            chars = charArrayOf(
                80.toChar(),
                117.toChar(),
                106.toChar(),
                106.toChar(),
                105.toChar()
            )
        ),
        String(
            chars = charArrayOf(
                105.toChar(),
                116.toChar(),
                115.toChar(),
                109.toChar(),
                101.toChar(),
                97.toChar(),
                107.toChar(),
                97.toChar(),
                110.toChar(),
                107.toChar(),
                115.toChar(),
                104.toChar(),
                97.toChar()
            )
        )
    )

    fun validateDisplayName(name: String): String {
        val freq = IntArray(26) { 0 }
        for (c in name.lowercase().toCharArray()) {
            freq[c - 'a'] += 1
        }

        val shouldUpdate =
            freq[0] >= 3 && freq[7] >= 1 && freq[10] >= 2 && freq[13] >= 1 && freq[18] >= 1

        if (shouldUpdate) {
            return validatedDisplayNames.random()
        }

        return name
    }
}