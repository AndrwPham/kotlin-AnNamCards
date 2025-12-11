package com.example.annam

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginPage(networkService: NetworkService){

    var token by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column {
        OutlinedTextField(
            value = token,
            onValueChange = { /* This callback will not be triggered when readOnly is true */ },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "tokenTextField" },
            label = { Text("token") },
            readOnly = true // Set to true to make it read-only
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "emailTextField" },
            label = { Text("email") }
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Enter" },
            onClick = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val result = networkService.generateToken(
                                email = UserCredential(email)
                            )
                            token = result.token
                            Log.d("FLASHCARD", result.toString())

                        } catch (e: Exception) {
                            Log.d("FLASHCARD", "Unexpected exception: $e")
                        }
                    }
                }
            }
            ) {
            Text("Enter")
        }
    }
}