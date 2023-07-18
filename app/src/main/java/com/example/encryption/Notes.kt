package com.example.encryption

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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

class Notes : ComponentActivity() {
    var data: MutableCollection<Note> = mutableSetOf()
    lateinit var pin: String
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pin = intent.getStringExtra("pin").toString()

        context = this@Notes

        // Initialize the data variable
        data = pin?.let { getFileData(it,context) }!!

        setContent {
            EncryptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EncryptionTheme {
                            TextOut("Notes")
                            for (d in data)
                                ConversationCard(d.title,d.content.take(5) + "...","*")
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .wrapContentHeight(align = Alignment.Bottom)
                        ) {
                            if (pin != null) {
                                AddConversation("+",pin)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Update the data
        data = pin?.let { getFileData(it,context) }!!

        // Invalidate the UI
        setContent {
            EncryptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EncryptionTheme {
                            TextOut("Notes")
                            for (d in data)
                                ConversationCard(d.title,d.content.take(5) + "...","*")
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .wrapContentHeight(align = Alignment.Bottom)
                        ) {
                            if (pin != null) {
                                AddConversation("+",pin)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationCard(name: String, lastMessage: String, notification: String, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .background(Color.Red)
            .fillMaxWidth(1f)
            .height(64.dp)
            .border(4.dp, Color.Black)
            .padding(8.dp)
    ) {
        Text(
            text = name,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(1f)
                .background(Color.Blue)
                .border(4.dp, Color.Black)
                .padding(8.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
        Text(
            text = lastMessage,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight(1f)
                .background(Color.Blue)
                .border(4.dp, Color.Black)
                .padding(8.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
        Text(
            text = notification,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .background(Color.Blue)
                .border(4.dp, Color.Black)
                .padding(8.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
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
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(Color.Blue),
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
            ConversationCard("convo1","hi","*")
            ConversationCard("convo2","yo i was...","*")
            ConversationCard("convo3","","")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentHeight(align = Alignment.Bottom)
        ) {
            AddConversation("+","1234123412341234")
        }
    }
}