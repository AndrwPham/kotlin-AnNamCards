package com.example.annam


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.unit.dp


@Composable
fun Menu(navigator: NavHostController) {
    val scope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext
    var userMessage by remember { mutableStateOf("") }

    val changeMessage: (String) -> Unit = { email ->
        userMessage = if (email.isBlank()) "Not logged in" else "Logged in as: $email"
    }

    LaunchedEffect(Unit) {
        val preferences = appContext.dataStore.data.first()
        changeMessage(preferences[EMAIL].orEmpty())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        // 🔝 Title at top
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AnNam",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (userMessage.isNotBlank()) {
                Text(
                    text = userMessage,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // 🔘 Buttons centered
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Button(
                    modifier = Modifier.semantics { contentDescription = "navToAddCard" },
                    onClick = { navigator.navigate(AddCardRoute) }
                ) {
                    Text("Add Card",
                        fontSize = 25.sp)
                }
            Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navigator.navigate(SearchScreenRoute) }) {
                    Text("Search Card",
                        fontSize = 25.sp)
                }
            Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navigator.navigate(StudyCardsRoute) }) {
                    Text("Study Cards",
                        fontSize = 25.sp)
                }
            Spacer(modifier = Modifier.height(16.dp))


                Button(
                    modifier = Modifier.semantics { contentDescription = "navToLogin" },
                    onClick = { navigator.navigate(LoginRoute) }
                ) {
                    Text("Login",
                        fontSize = 25.sp)
                }
            Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.semantics { contentDescription = "ExecuteLogout" },
                    onClick = {
                        scope.launch {
                            appContext.dataStore.edit {
                                it.remove(EMAIL)
                                it.remove(TOKEN)
                            }
                            changeMessage("")
                        }
                    }
                ) {
                    Text(
                        "Log out",
                        fontSize = 25.sp,
                        modifier = Modifier.semantics { contentDescription = "Logout" }
                    )
                }
            }
        }
    }


