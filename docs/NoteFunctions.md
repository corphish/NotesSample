# Note Functions
Note related functions that are specific to an `User`. To get an instance to of the `NoteFunctions`, call the following:
```kotlin
val noteFunctions = NotesCore.noteFunctions(user)
```

Where the `user` is an `User` object derived earlier. Please check the [UserFunctions](UserFunctions.md) for more info.

#### Getting the list of all notes created by the user
We can use the following method to get all the notes created by the user:
```kotlin
val notesFlow = noteFunctions.getAllNotes()
```

This returns an object of `Flow<List<Note>>`. As this returns a `Flow<>`, this can be easily consumed in Jetpack Compose by observing at as state.
```kotlin
val notes by noteFunctions.getAllNotes().collectAsState(emptyList())
```

The `notes` here is now of type `List<Note>` which can now be directly shown in a `LazyColumn`. Any updates to this list (like insertion of new note or deletion of existing note) will automatically be reflected in this `List` without any additional work. Note that this `List<Note>` can be empty but never be null.

#### New note creation
In order to create a note, simply request the `summary` and `details` of the note as user input and pass to the `NoteFunctions`.
```kotlin
val note = noteFunctions.insertNote(summary, details)
```

This returns the newly created note.

#### Updating existing note
To update an existing note, we need the reference of the old `Note` (which we are going to update) and the updated `summary` and `details` (like we requested for creating new note).
```kotlin
val newNote = noteFunctions.updateNote(oldNote, summary, details)
```

This returns the updated note.

#### Deleting existing note
To delete an existing note, we need the reference of the `Note` that we are going to delete, which can done like this.
```kotlin
noteFunctions.deleteNote(note)
```

This returns a `boolean` telling if note deletion was successful.

### Examples
Some examples of `NoteFunctions` can be found in [NotesActivity](../app/src/main/java/com/corphish/notessample/NotesActivity.kt) and [NoteCreationActivity](../app/src/main/java/com/corphish/notessample/NoteCreationActivity.kt). 