package com.example.annam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun Menu(navigator: NavHostController) {
    val scope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext
    var userMessage by remember { mutableStateOf("") }
    val changeMessage = fun (email: String) {
        userMessage = if (email.isBlank()) "Not logged in" else "Logged in as: $email"
    }
    LaunchedEffect(Unit) {
        val preferencesFlow: Flow<Preferences> = appContext.dataStore.data
        val preferences = preferencesFlow.first()
        changeMessage(preferences[EMAIL].orEmpty())
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AnNam",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        if (userMessage.isNotBlank()) {
            Text(text = userMessage)
        }

        Button(
            modifier = Modifier.semantics { contentDescription = "navToAddCard" },
            onClick = {
                navigator.navigate(AddCardRoute)
            }
        ) {
            Text("Add Card")
        }

        Button(onClick = {
            navigator.navigate(SearchScreenRoute)
        }) {
            Text("Search Card")
        }

        Button(onClick = {
            navigator.navigate(StudyCardsRoute)
        }) {
            Text("Study Cards")
        }

        Button(
            modifier = Modifier.semantics { contentDescription = "navToLogin" },
            onClick = {
                navigator.navigate(LoginRoute)
            }
        ) {
            Text("Login")
        }

        Button(
            modifier = Modifier.semantics { contentDescription = "ExecuteLogout" },
            onClick = {
                scope.launch {
                    appContext.dataStore.edit { preferences ->
                        preferences.remove(EMAIL)
                        preferences.remove(TOKEN)
                    }
                    changeMessage("")
                }
            }
        ) {
            Text(
                "Log out",
                modifier = Modifier.semantics { contentDescription = "Logout" }
            )
        }
    }
}
