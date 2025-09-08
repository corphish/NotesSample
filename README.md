# Notes Sample
This is an android app which demonstrates a basic note taking app which uses the `NotesCore` library which provides all the functions. The Android app is done using Jetpack Compose.

## NotesCore
All the functions related to notes are provided by this library. Following functions are supported:
- User management - Creation and authentication of users.
- Session management - Login and logout functionality for users with timeout.
- Notes management - Creation, deletion and updating notes specific to user.

#### Get started with the NotesCore library
Download the latest `notescore.aar` from the releases section, put it inside `app/libs/` folder (create the `libs` folder if it does not exist), and then import them into the project. To do that, add the following line in the app level `build.gradle` (`app/build.gradle`):
```kotlin
implementation(files("libs/notescore.aar"))
```

And sync the project.

#### Setup the library in app
The `NotesCore` library must be initialized in a custom application class as follows.
```kotlin
NotesCore.init(this)
```

An example of custom application class can be found [here](app/src/main/java/com/corphish/notessample/NotesApplication.kt). Do not forget to register the custom application class in manifest as it is done [here](https://github.com/corphish/NotesSample/blob/main/app/src/main/AndroidManifest.xml#L6).

### Further usage
- [UserFunctions](docs/UserFunctions.md) - Functions to handle users and sessions.
- [NoteFunctions](docs/NoteFunctions.md) - Functions to manage notes for an user.

### Sample app
A sample app is also part of the release (app-debug.apk) which uses this library and demonstrates its functions.