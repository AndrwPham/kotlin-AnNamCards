@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.annam

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.annam.ui.theme.AnNamTheme
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

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
                Navigator(navController = NavHostController(this),networkService)
            }

        }
    }
}
@Composable
fun Menu(navigator: NavHostController) {
    //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

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
            navigator.navigate("play")
        }) {
            Text("Play")
        }

        Button(modifier= Modifier.semantics{contentDescription = "navToLogin"},
            onClick = {
            navigator.navigate("loginPage")
        }){
            Text("Login")
        }
    }
}
