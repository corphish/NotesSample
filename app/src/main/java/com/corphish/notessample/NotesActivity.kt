package com.corphish.notessample

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.corphish.notescore.api.NotesCore
import com.corphish.notescore.api.functions.NoteFunctions
import com.corphish.notescore.models.User
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
                                onClick = {  },
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
                            onUserLoggedOut = {
                                lifecycleScope.launch {
                                    userFunctions.logoutUser(it)

                                    // Go to login page
                                    val intent = Intent(this@NotesActivity, MainActivity::class.java)
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
    onUserLoggedOut: (User) -> Unit = {},
    modifier: Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            UserBanner(
                user = user,
                onUserLoggedOut = onUserLoggedOut
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
        modifier = Modifier.padding(all = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            Text(greetings[(Math.random() * greetings.size).toInt()], style = MaterialTheme.typography.headlineMedium)
            Text(user.displayName)
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