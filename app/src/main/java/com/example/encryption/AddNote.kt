package com.example.encryption

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.encryption.ui.theme.EncryptionTheme

class AddConversation : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EncryptionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current // Get the context
                    AddNoteScreen(context)
                }
            }
        }
    }
}

fun onBackPressed(context: Context) {
    // Handle back navigation here
    // For example, you can call finish() on the activity
    (context as? AddConversation)?.finish()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(context: Context) { // Pass the context as a parameter
    var title by remember { mutableStateOf(TextFieldValue()) }
    var content by remember { mutableStateOf(TextFieldValue()) }

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Handle back navigation when the navigation icon is clicked
    val onBackPressedCallback = remember(onBackPressedDispatcher) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // You can perform any additional logic before navigating back here
                onBackPressed(context) // Call the function and pass the context
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
                        value = title,
                        onValueChange = { title = it },
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
            onValueChange = { content = it },

            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            label = { Text("Enter Note Content") } // Placeholder for the content input field
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddNoteScreenPreview() {
    EncryptionTheme {
        val context = LocalContext.current
        AddNoteScreen(context)
    }
}