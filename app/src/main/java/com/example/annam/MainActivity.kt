@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.annam

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.annam.ui.theme.AnNamTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val Context.dataStore by preferencesDataStore(
    name = "user_credentials"
)

//Because Preferences DataStore doesn't use a predefined schema,
//you must use the corresponding key type function to define a key for each value that you need to store
//in the DataStore<Preferences> instance.
//For example, to define a key for an int value, use intPreferencesKey()
val TOKEN = stringPreferencesKey("token")
val EMAIL = stringPreferencesKey("email")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appContext = applicationContext
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()
            val flashCardDao = db.flashCardDao()
            runBlocking {
                val flashCards= flashCardDao.getAll()
                Log.d("TEST", "All flashcards: $flashCards")
            }
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://placeholder.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val networkService = retrofit.create(NetworkService::class.java)
            AnNamTheme {
                Navigator(
                    navController = NavHostController(this),
                    networkService = networkService,
                    flashCardDao = flashCardDao
                )
            }

        }
    }
}
@Composable
fun Menu(navigator: NavHostController) {
    //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    val scope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext;
    LaunchedEffect(Unit) {

        //Then, use the DataStore.data property to expose the appropriate stored value using a Flow.

        //In coroutines, a flow is a type that can emit multiple values sequentially,
        //as opposed to suspend functions that return only a single value.
        //For example, you can use a flow to receive live updates from a database.

        //Flows are built on top of coroutines and can provide multiple values.
        //A flow is conceptually a stream of data that can be computed asynchronously.
        //The emitted values must be of the same type. For example, a Flow<Int>
        //is a flow that emits integer values.

        //In Kotlin with Jetpack DataStore, the Flow<Preferences> returned by dataStore.data
        // emits every time any single preference within the DataStore file changes.
        //The flow emits the entire Preferences object, containing all current key-value pairs, with each change.

        //In Kotlin Flow, the first() terminal operator is used to collect only the initial value emitted
        //by a flow and then automatically cancel the flow's execution.
        //This is particularly useful in Jetpack Compose and other Android development scenarios
        //where you only need a single, immediate result from a potentially long-running data stream.

        val preferencesFlow: Flow<Preferences> = appContext.dataStore.data
        val preferences = preferencesFlow.first()
      //  changeMessage(preferences[EMAIL] ?: "")

    }

    Column(
//        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AnNam",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Button( modifier = Modifier.semantics{contentDescription = "navToAddCard"},
            onClick = {
            navigator.navigate("add_card")
        }){
            Text("Add Card")
        }

        Button(onClick = {
            navigator.navigate("search_card")
        }){
            Text("Search Card")
        }

        Button(onClick = {
            navigator.navigate("study_card")
        }) {
            Text("Study Cards")
        }

        Button(modifier= Modifier.semantics{contentDescription = "navToLogin"},
            onClick = {
            navigator.navigate("loginPage")
        }){
            Text("Login")
        }

        Button(
            modifier = Modifier
                .semantics { contentDescription = "ExecuteLogout" }, onClick = {

                scope.launch {
                    appContext.dataStore.edit { preferences ->
                        preferences.remove(EMAIL)
                        preferences.remove(TOKEN)
                       // changeMessage(preferences[EMAIL] ?: "")
                    }
                }

            }) {
            Text(
                "Log out",
                modifier = Modifier.semantics { contentDescription = "Logout" }
            )
        }
    }
}

