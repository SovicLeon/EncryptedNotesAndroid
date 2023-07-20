package com.leonsovic.encryption

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leonsovic.encryption.ui.theme.EncryptionTheme
import androidx.compose.material.Colors
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip


class Notes : ComponentActivity() {
    var data: MutableCollection<Note> = mutableSetOf()
    lateinit var pin: String
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pin = intent.getStringExtra("pin").toString()
        context = this@Notes
        data = pin?.let { getFileData(it, context) }!!

        setContent {
            NotesContent()
        }
    }

    @Composable
    fun NotesContent() {
        EncryptionTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(0.9f),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EncryptionTheme {
                            TextOut("Notes")
                            ScrollItems()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    ) {
                        if (pin != null) {
                            AddNote("Add note", pin)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        data = pin?.let { getFileData(it, context) }!!

        setContent {
            NotesContent()
        }
    }

    @Composable
    fun ScrollItems() {
        val list = data.toList()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(list) { note ->
                NoteCard(note.title, note.content.take(5) + "...", note.date.date.toString() + "." + (note.date.month+1).toString() + "." + (note.date.year-100+2000).toString(), list.indexOf(note), {
                    onDeleteNote()
                })
                Log.d("FileContent", list.indexOf(note).toString())
            }
        }
    }

    private fun onDeleteNote() {
        data = pin?.let { getFileData(it, context) }!!
        setContent {
            NotesContent()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun NoteCard(name: String, lastMessage: String, notification: String, index: Int,onDeleteNote: () -> Unit, modifier: Modifier = Modifier) {
        val showDialog = remember { mutableStateOf(false) }
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                //.background(Color.hsv(176f, 0.66f, 0.68f))
                .fillMaxWidth()
                .height(64.dp)
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.primary)
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = {
                        val intent = Intent(context, AddNote::class.java).apply {
                            putExtra("pin", pin)
                            putExtra("id", index)
                        }
                        context.startActivity(intent)
                    },
                    onLongClick = {
                        showDialog.value = true
                    },
                )
        ) {
            Text(
                text = name,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .fillMaxHeight(1f)
                    .padding(8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = lastMessage,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(1f)
                    .padding(8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = notification,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(1f)
                    .padding(8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (showDialog.value) {
            DeleteWindow(
                onConfirmDelete = {
                    data.remove(data.elementAt(index))
                    saveDataToFile(pin, context, data)
                    showDialog.value = false
                    onDeleteNote()
                },
                onDismiss = {
                    showDialog.value = false
                }
            )
        }
    }

    @Composable
    fun DeleteWindow(onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
        Column (
            modifier = Modifier
                .border(2.dp,Color.Red)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Are you sure you want to delete?",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        onConfirmDelete()
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .fillMaxWidth(0.5f)
                ) {
                    Text("Yes")
                }

                Button(
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .fillMaxWidth(1f)
                ) {
                    Text("No")
                }
            }
        }
    }
}


@Composable
fun AddNote(name: String, pin: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Button(onClick = {
            val intent = Intent(context, AddNote::class.java).apply {
                putExtra("pin", pin)
                putExtra("id", -1)
            }
            context.startActivity(intent)
         },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth(1f)
            .height(64.dp)
            .padding(8.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    )
    {
        Text(
            text = name
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EncryptionTheme {
            TextOut("Notes")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentHeight(align = Alignment.Bottom)
        ) {
            AddNote("Add note","1234123412341234")
        }
    }
}