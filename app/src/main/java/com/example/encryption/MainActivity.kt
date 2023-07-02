package com.example.encryption

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.encryption.ui.theme.EncryptionTheme
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import javax.crypto.spec.SecretKeySpec

import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EncryptionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val pinValue = remember { mutableStateOf(TextFieldValue("")) }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextOut("Enter PIN")
                        TextFieldPIN(pinValue.value) { newValue ->
                            pinValue.value = newValue
                        }
                        ButtonLogin(pinValue.value) { pin ->
                            Log.d("pin", pin)
                            val context = this@MainActivity
                            manageFile(context, pin)
                        }
                        Spacer(modifier = Modifier.height(128.dp))
                    }
                }
            }
        }
    }
}

fun manageFile(context: Context, pin: String) {
    val files = context.fileList()

    val pinFinal = padPin(pin)

    val encryptedFilename = encryptData("filename",pinFinal)
    Log.d("pin", "encFile: $encryptedFilename")

    if (files.contains(encryptedFilename)) {
        // File exists
        Log.d("FileStatus", "File '$encryptedFilename' exists.")

        // Read the file content
        val fileContent = context.openFileInput(encryptedFilename).bufferedReader().use {
            it.readText()
        }

        Log.d("FileContent", decryptData(fileContent,pinFinal))
    } else {
        // File does not exist
        Log.d("FileStatus", "File '$encryptedFilename' does not exist.")

        // Create the file
        createFileInInternalStorage(context, pin)
    }

    val intent = Intent(context, Conversations::class.java).apply {
        putExtra("pin", pinFinal)
    }
    context.startActivity(intent)
}

fun createFileInInternalStorage(context: Context, pin: String) {
    val pinFinal = padPin(pin)

    val encryptedFilename = encryptData("filename",pinFinal)

    val fileContents = encryptData("123;2023-06-17-18:00;0test;1hi;0kaj;1nic+444;2023-06-17-18:11;0test;1aaa;0kaj;1aaa",pinFinal)

    context.openFileOutput(encryptedFilename, Context.MODE_PRIVATE).use { fileOutputStream ->
        fileOutputStream.write(fileContents.toByteArray())
    }

    // Read from file
    val fileContent = context.openFileInput(encryptedFilename).bufferedReader().use {
        it.readText()
    }

    // Print the file content
    Log.d("FileContent", fileContent)
    Log.d("FileContent", decryptData(fileContent,pinFinal))
}

fun padPin(pin: String): String {
    val paddedPin = StringBuilder(pin)
    while (paddedPin.length < 16) {
        paddedPin.append("0")
    }
    return paddedPin.toString()
}

fun encryptData(data: String, key: String): String {
    return try {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        Base64.encodeToString(encryptedBytes,0)
    } catch (e: Exception) {
        // Handle encryption error
        Log.d("error", e.message.toString())
        e.printStackTrace()
        ""
    }
}

fun decryptData(data: String, key: String): String {
    return try {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(data,0)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        String(decryptedBytes)
    } catch (e: Exception) {
        // Handle decryption error
        Log.d("error", e.message.toString())
        e.printStackTrace()
        ""
    }
}

@Composable
fun TextOut(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = Modifier
            .padding(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldPIN(text: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = text,
        label = { Text(text = "PIN") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        modifier = Modifier
            .height(64.dp)
    )
}

@Composable
fun ButtonLogin(pinValue: TextFieldValue, onClick: (String) -> Unit) {
    Button(onClick = {
        onClick(pinValue.text) },
        shape = RoundedCornerShape(4.dp))
    {
        Text(text = "Login")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        EncryptionTheme {
            TextOut("Enter PIN")
        }
    }
}