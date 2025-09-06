package com.corphish.notessample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.corphish.notescore.api.NotesCore
import com.corphish.notescore.api.functions.UserFunctions
import com.corphish.notescore.models.User
import com.corphish.notessample.ui.theme.NotesSampleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userFunctions = NotesCore.userFunctions()

        setContent {
            NotesSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EntryPoint(
                        userFunctions = userFunctions,
                        onUserAvailableToProceed = {
                            val intent = Intent(this, NotesActivity::class.java)
                            intent.putExtra(NotesActivity.KEY_USER, it)
                            startActivity(intent)
                            finish()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun EntryPoint(
    userFunctions: UserFunctions,
    onUserAvailableToProceed: (User) -> Unit = {  },
    modifier: Modifier
) {
    // 0 - Login, 1 - Register, -1 - Checking for session
    var mode by remember { mutableIntStateOf(-1) }
    val scope = rememberCoroutineScope()

    // Check for currently logged in user
    LaunchedEffect(Unit) {
        scope.launch {
            val currentlyLoggedInUser = userFunctions.getCurrentlyLoggedInUser()
            if (currentlyLoggedInUser != null) {
                onUserAvailableToProceed(currentlyLoggedInUser)
            } else {
                mode = 0
            }
        }
    }

    when (mode) {
        -1 -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        0 -> Login(
            userFunctions = userFunctions,
            modifier = modifier,
            onLoginSuccess = onUserAvailableToProceed,
            onRegisterClicked = { mode = 1 }
        )

        1 -> Register(
            userFunctions = userFunctions,
            modifier = modifier,
            onLoginClicked = { mode = 0 }
        )
    }
}

@Composable
fun Register(
    userFunctions: UserFunctions,
    modifier: Modifier,
    onLoginClicked: () -> Unit = { }
) {
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val user = userFunctions.registerUser(username, displayName, password)
                    isError = user == null
                    Log.i(
                        "Main",
                        if (user == null) "User registration failed" else "User registration successful: $user"
                    )

                    if (!isError) {
                        onLoginClicked()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onLoginClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}

@Composable
fun Login(
    userFunctions: UserFunctions,
    modifier: Modifier,
    onLoginSuccess: (User) -> Unit = { },
    onRegisterClicked: () -> Unit = { }
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val user = userFunctions.authenticateUser(username, password)
                    isError = user == null
                    Log.i(
                        "Main",
                        if (user == null) "User authentication failed" else "User authentication successful: $user"
                    )

                    if (user != null) {
                        userFunctions.loginUser(user)
                        onLoginSuccess(user)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onRegisterClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesSampleTheme {
        Greeting("Android")
    }
}