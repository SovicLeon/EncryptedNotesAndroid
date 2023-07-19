package com.example.encryption

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.encryption.ui.theme.EncryptionTheme
import java.util.Date

var id: Int = -1
var data: MutableCollection<Note> = mutableSetOf()
lateinit var currentItem: Note
lateinit var pin: String
class AddNote : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        currentItem = Note("","", Date())
        super.onCreate(savedInstanceState)

        pin = intent.getStringExtra("pin").toString()

        id = intent.getIntExtra("id", -1)

        Log.d("FileContent", id.toString())
        Log.d("FileContent", pin)

        val context = this@AddNote

        data = getFileData(pin,context)

        if (id > -1) {
            currentItem = data.elementAt(id)
        }

        setContent {
            EncryptionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddNoteScreen(context)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(context: Context) { // Pass the context as a parameter
    var title by remember { mutableStateOf(TextFieldValue(currentItem.title)) }
    var content by remember { mutableStateOf(TextFieldValue(currentItem.content)) }

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Handle back navigation when the navigation icon is clicked
    val onBackPressedCallback = remember(onBackPressedDispatcher) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // You can perform any additional logic before navigating back here
                (context as? AddNote)?.finish()
            }
        }
    }

    SideEffect {
        onBackPressedCallback.isEnabled = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // App Bar with back arrow and title input field
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { onBackPressedCallback.handleOnBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back Arrow")
                    }
                    OutlinedTextField(
                        value = title, // This is the change
                        onValueChange = { updatedTitle ->
                            title = updatedTitle
                            parseTitle(updatedTitle.text, context)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp), // Occupy remaining width
                        label = { Text("Enter Title") } // Placeholder for the title input field
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )

        // Text input field for the rest of the content
        OutlinedTextField(
            value = content,
            onValueChange = { updatedContent ->
                content = updatedContent
                parseContent(updatedContent.text, context)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            label = { Text("Enter Note Content") } // Placeholder for the content input field
        )
    }
}

// Define your parsing functions here
fun parseTitle(title: String, context: Context) {
    currentItem.title = title

    if (id == -1) {
        data.add(currentItem)
        id = data.size-1
    } else {
        data.elementAt(id).title = title
    }
    saveDataToFile(pin, context, data)
}

fun parseContent(content: String, context: Context) {
    currentItem.content = content

    if (id == -1) {
        data.add(currentItem)
        id = data.size-1
    } else {
        data.elementAt(id).content = content
    }
    saveDataToFile(pin, context, data)
}

@Preview(showBackground = true)
@Composable
fun AddNoteScreenPreview() {
    EncryptionTheme {
        val context = LocalContext.current
        AddNoteScreen(context)
    }
}