package com.example.encryption

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.encryption.ui.theme.EncryptionTheme
import androidx.compose.material.Colors
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
            NotesContent(data)
        }
    }

    @Composable
    fun NotesContent(data: MutableCollection<Note>) {
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
                            ScrollItems(data, context, pin)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    ) {
                        if (pin != null) {
                            AddConversation("Add note", pin)
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
            NotesContent(data)
        }
    }
}

@Composable
fun ScrollItems(data: MutableCollection<Note>, context: Context, pin: String) {
    val list = data.toList()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(list) { note ->
            ConversationCard(note.title, note.content.take(5) + "...", note.date.date.toString() + "." + (note.date.month+1).toString() + "." + (note.date.year-100+2000).toString(), list.indexOf(note), context, pin)
            Log.d("FileContent", list.indexOf(note).toString())
        }
    }
}


@Composable
fun ConversationCard(name: String, lastMessage: String, notification: String, index: Int, context: Context, pin: String, modifier: Modifier = Modifier) {
    Button(onClick = {
        val intent = Intent(context, AddNote::class.java).apply {
                putExtra("pin", pin)
                putExtra("id", index)
        }
        context.startActivity(intent)
        },
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                //.background(Color.hsv(176f, 0.66f, 0.68f))
                .background(MaterialTheme.colorScheme.primary)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text(
                text = name,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .fillMaxHeight(1f)
                    .padding(8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            Text(
                text = lastMessage,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(1f)
                    .padding(8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            Text(
                text = notification,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(1f)
                    .padding(8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun AddConversation(name: String, pin: String, modifier: Modifier = Modifier) {
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
            AddConversation("Add note","1234123412341234")
        }
    }
}