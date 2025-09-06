package com.corphish.notessample

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.corphish.notescore.api.NotesCore
import com.corphish.notescore.api.functions.NoteFunctions
import com.corphish.notescore.models.Note
import com.corphish.notescore.models.User
import com.corphish.notescore.utils.TimeUtils
import com.corphish.notessample.ui.theme.NotesSampleTheme
import kotlinx.coroutines.launch

class NotesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_USER, User::class.java)
        } else {
            intent.getParcelableExtra(KEY_USER)
        }

        if (user == null) {
            setContent {
                NotesSampleTheme {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Unexpected error occurred",
                        )
                    }
                }
            }
        } else {
            val noteFunctions = NotesCore.noteFunctions(user)
            val userFunctions = NotesCore.userFunctions()

            setContent {
                NotesSampleTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    val intent = Intent(this, NoteCreationActivity::class.java)
                                    intent.putExtra(NoteCreationActivity.KEY_USER, user)
                                    startActivity(intent)
                                },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    ) { innerPadding ->
                        NotesHome(
                            user = user,
                            noteFunctions = noteFunctions,
                            onNoteClick = {
                                val intent = Intent(this, NoteCreationActivity::class.java)
                                intent.putExtra(NoteCreationActivity.KEY_USER, user)
                                intent.putExtra(NoteCreationActivity.KEY_NOTE, it)
                                startActivity(intent)
                            },
                            onUserLoggedOut = {
                                lifecycleScope.launch {
                                    userFunctions.logoutUser(it)

                                    // Go to login page
                                    val intent =
                                        Intent(this@NotesActivity, MainActivity::class.java)
                                    startActivity(intent)
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
        const val KEY_USER = "user_key"
    }
}

@Composable
fun NotesHome(
    user: User,
    noteFunctions: NoteFunctions,
    onNoteClick: (Note) -> Unit = { },
    onUserLoggedOut: (User) -> Unit = { },
    modifier: Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            UserBanner(
                user = user,
                onUserLoggedOut = onUserLoggedOut
            )

            Text(
                "Notes",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            NotesList(
                noteFunctions = noteFunctions,
                onNoteClick = onNoteClick
            )
        }
    }
}

val greetings = listOf(
    "Hi",
    "Hello",
    "Henlo",
    "Ciao",
    "Hola",
    "Namaste",
    "Nomoskar"
)

@Composable
fun UserBanner(
    user: User,
    onUserLoggedOut: (User) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(all = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            Text(
                greetings[(Math.random() * greetings.size).toInt()],
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.W200
            )
            Text(user.displayName, fontWeight = FontWeight.W500)
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { onUserLoggedOut(user) },
            modifier = Modifier.padding(all = 16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "logout")
        }
    }
}

@Composable
fun NotesList(
    noteFunctions: NoteFunctions,
    onNoteClick: (Note) -> Unit = { }
) {
    val notes by noteFunctions.getAllNotes().collectAsState(emptyList())
    val scope = rememberCoroutineScope()

    if (notes.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Notes created will be shown here", style = MaterialTheme.typography.bodySmall)
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            items(notes) {
                NoteItem(
                    note = it,
                    onNoteClick = onNoteClick,
                    onNoteDeleted = {
                        scope.launch {
                            noteFunctions.deleteNote(it)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onNoteClick: (Note) -> Unit = { },
    onNoteDeleted: (Note) -> Unit = { }
) {
    Card(
        onClick = { onNoteClick(note) },
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.List,
                contentDescription = "list icon",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(note.summary, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Modified ${TimeUtils.getTimeAgo(note.creationTime)}",
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { onNoteDeleted(note) }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "delete",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}