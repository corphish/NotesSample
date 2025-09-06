package com.corphish.notessample

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.corphish.notescore.api.NotesCore
import com.corphish.notescore.models.Note
import com.corphish.notescore.models.User
import com.corphish.notessample.NotesActivity.Companion
import com.corphish.notessample.ui.theme.NotesSampleTheme
import kotlinx.coroutines.launch

class NoteCreationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_USER, User::class.java)
        } else {
            intent.getParcelableExtra(KEY_USER)
        }

        val note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_NOTE, Note::class.java)
        } else {
            intent.getParcelableExtra(KEY_NOTE)
        }

        if (user == null) {
            setContent {
                NotesSampleTheme {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Unexpected error occurred",
                        )
                    }
                }
            }
        } else {
            val noteFunctions = NotesCore.noteFunctions(user)

            setContent {
                NotesSampleTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                    ) { innerPadding ->
                        NoteInteraction(
                            note = note,
                            onSave = { summary, details ->
                                lifecycleScope.launch {
                                    if (note == null) {
                                        noteFunctions.insertNote(
                                            summary = summary,
                                            details = details
                                        )
                                    } else {
                                        noteFunctions.updateNote(
                                            oldNote = note,
                                            newSummary = summary,
                                            newDetails = details
                                        )
                                    }
                                    finish()
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_USER = "user"
        const val KEY_NOTE = "note"
    }
}

@Composable
fun NoteInteraction(
    note: Note?,
    onSave: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier
) {
    var summary by remember { mutableStateOf(note?.summary ?: "") }
    var details by remember { mutableStateOf(note?.details ?: "") }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = summary,
            onValueChange = { summary = it },
            label = { Text("Summary") },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("Details") },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .weight(1f)
        )

        Button(
            onClick = { onSave(summary, details) },
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}