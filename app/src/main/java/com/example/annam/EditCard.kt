package com.example.annam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EditCard(
    navBack: () -> Unit,
    flashCardDao: FlashCardDao,
    english: String,
    vietnamese: String
) {
    var enWord by remember { mutableStateOf("") }
    var vnWord by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(english, vietnamese) {
        isLoading = true
        errorMessage = null
        try {
            val card = withContext(Dispatchers.IO) {
                flashCardDao.findByCards(english, vietnamese)
            }
            if (card == null) {
                errorMessage = "Card not found."
                enWord = ""
                vnWord = ""
            } else {
                enWord = card.englishCard.orEmpty()
                vnWord = card.vietnameseCard.orEmpty()
            }
        } catch (e: Exception) {
            errorMessage = "Load failed: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column {
        Button(
            modifier = Modifier.align(Alignment.Start),
            onClick = navBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> Text("Loading...")
            errorMessage != null -> Text(errorMessage!!)
            else -> {
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
            }
        }
    }
}
