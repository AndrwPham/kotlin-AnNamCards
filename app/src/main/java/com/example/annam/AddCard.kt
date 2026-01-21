package com.example.annam

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddCard(
    navBack: () -> Unit,
    flashCardDao: FlashCardDao
) {
    var enWord by remember { mutableStateOf("") }
    var vnWord by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column {
        Button(
            modifier = Modifier.align(Alignment.Start),
            onClick = navBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = enWord,
            onValueChange = { enWord = it },
            label = { Text("en") }
        )

        TextField(
            value = vnWord,
            onValueChange = { vnWord = it },
            label = { Text("vn") }
        )

        Button(onClick = {
            val english = enWord.trim()
            val vietnamese = vnWord.trim()
            if (english.isEmpty() || vietnamese.isEmpty()) {
                Log.d("demo", "Skip insert: blank English or Vietnamese value")
                statusMessage = "English and Vietnamese are required."
                return@Button
            }
            scope.launch(Dispatchers.IO) {
                try {
                    val insertResult = flashCardDao.insert(
                        FlashCard(
                            uid = 0,
                            englishCard = english,
                            vietnameseCard = vietnamese
                        )
                    )
                    val inserted = insertResult != -1L
                    Log.d("demo", "Insert result inserted=$inserted for $english / $vietnamese")
                    withContext(Dispatchers.Main) {
                        statusMessage = if (inserted) {
                            enWord = ""
                            vnWord = ""
                            "Flashcard added."
                        } else {
                            "Flashcard already exists."
                        }
                    }
                } catch (e: Exception) {
                    Log.d("demo", "Insert failed: $e")
                    withContext(Dispatchers.Main) {
                        statusMessage = "Insert failed: ${e.message}"
                    }
                }
            }
        }) {
            Text("Add")
        }

        statusMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it)
        }
    }
}
