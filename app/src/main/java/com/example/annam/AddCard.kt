package com.example.annam

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
                return@Button
            }
            scope.launch(Dispatchers.IO) {
                try {
                    flashCardDao.insertAll(
                        FlashCard(
                            uid = 0,
                            englishCard = english,
                            vietnameseCard = vietnamese
                        )
                    )
                } catch (e: Exception) {
                }
                withContext(Dispatchers.Main) {
                    enWord = ""
                    vnWord = ""
                }
            }
        }) {
            Text("Add")
        }
    }
}
