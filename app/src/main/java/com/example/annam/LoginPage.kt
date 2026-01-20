package com.example.annam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
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
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginPage(
    networkService: NetworkService,
    navigator: NavHostController
){

    var token by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column {

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
                if (email.isBlank()) {
                    return@Button
                }
                scope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            networkService.generateToken(
                                email = UserCredential(email)
                            )
                        }
                       // token = "Code: ${result.code}, Message: ${result.message}"
                        if (result.code == 200) {
                            navigator.navigate(TokenRoute(email))
                        }
                    } catch (e: Exception) {
                      //  token = "Error: ${e.message}"
                    }
                }
            }
            ) {
            Text("Enter")
        }
    }
}
