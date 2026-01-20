@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.annam

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
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
