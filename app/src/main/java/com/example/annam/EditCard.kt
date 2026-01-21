package com.example.annam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun EditCard(
    navBack: () -> Unit,
    flashCardDao: FlashCardDao,
    english: String,
    vietnamese: String
) {
    var enWord by remember { mutableStateOf("") }
    var vnWord by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var cardUid by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(english, vietnamese) {
        isLoading = true
        errorMessage = null
        statusMessage = null
        try {
            val card = withContext(Dispatchers.IO) {
                flashCardDao.findByCards(english, vietnamese)
            }
            enWord = card.englishCard.orEmpty()
            vnWord = card.vietnameseCard.orEmpty()
            cardUid = card.uid
        } catch (e: Exception) {
            errorMessage = "Load failed: ${e.message}"
            enWord = ""
            vnWord = ""
            cardUid = null
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
        .safeDrawingPadding()
    ) {

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

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    enabled = !isSaving,
                    onClick = {
                        val englishValue = enWord.trim()
                        val vietnameseValue = vnWord.trim()
                        if (englishValue.isEmpty() || vietnameseValue.isEmpty()) {
                            statusMessage = "English and Vietnamese are required."
                            return@Button
                        }
                        val uid = cardUid
                        if (uid == null) {
                            statusMessage = "Card not loaded."
                            return@Button
                        }
                        isSaving = true
                        statusMessage = null
                        scope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    flashCardDao.update(
                                        FlashCard(
                                            uid = uid,
                                            englishCard = englishValue,
                                            vietnameseCard = vietnameseValue
                                        )
                                    )
                                }
                                statusMessage = "Updated."
                            } catch (e: Exception) {
                                statusMessage = "Update failed: ${e.message}"
                            } finally {
                                isSaving = false
                                navBack()
                            }
                        }
                    }
                ) {
                    Text(if (isSaving) "Updating..." else "Update")
                }

                statusMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it)
                }
            }
        }
    }
}
