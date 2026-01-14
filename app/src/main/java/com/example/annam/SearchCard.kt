package com.example.annam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
fun SearchCard(navBack: () -> Unit,
               flashCardDao: FlashCardDao){
    var enWord by remember { mutableStateOf("") }
    var vnWord by remember { mutableStateOf("") }
    var isCheckedEnglish by remember { mutableStateOf(false) }
    var isCheckedVietnamese by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun performSearch() {
        val englishQuery = enWord.trim()
        val vietnameseQuery = vnWord.trim()
        val englishExact = isCheckedEnglish
        val vietnameseExact = isCheckedVietnamese
        if (englishQuery.isEmpty() && vietnameseQuery.isEmpty()) {
            searchResults = emptyList()
            statusMessage = "Enter a search term."
            return
        }
        isSearching = true
        statusMessage = null
        scope.launch {
            try {
                val matches = withContext(Dispatchers.IO) {
                    flashCardDao.getAll().filter { card ->
                        val englishMatch = if (englishQuery.isBlank()) {
                            true
                        } else {
                            val value = card.englishCard.orEmpty()
                            if (englishExact) {
                                value.equals(englishQuery, ignoreCase = true)
                            } else {
                                value.contains(englishQuery, ignoreCase = true)
                            }
                        }
                        val vietnameseMatch = if (vietnameseQuery.isBlank()) {
                            true
                        } else {
                            val value = card.vietnameseCard.orEmpty()
                            if (vietnameseExact) {
                                value.equals(vietnameseQuery, ignoreCase = true)
                            } else {
                                value.contains(vietnameseQuery, ignoreCase = true)
                            }
                        }
                        englishMatch && vietnameseMatch
                    }
                }
                searchResults = matches
                statusMessage = if (matches.isEmpty()) "No matches found." else null
            } catch (e: Exception) {
                statusMessage = "Search failed: ${e.message}"
                searchResults = emptyList()
            } finally {
                isSearching = false
            }
        }
    }

    Column{
        Button(
            modifier = Modifier.align(Alignment.Start),
            onClick = navBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isCheckedEnglish,
                onCheckedChange = { isCheckedEnglish = it }
            )
            TextField(
                value = enWord,
                onValueChange = { enWord = it },
                label = { Text("en") }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isCheckedVietnamese,
                onCheckedChange = { isCheckedVietnamese = it }
            )
            TextField(
                value = vnWord,
                onValueChange = { vnWord = it },
                label = { Text("en") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            enabled = !isSearching,
            onClick = { performSearch() }
        ) {
            Text(if (isSearching) "Searching..." else "Search")
        }

        Spacer(modifier = Modifier.height(12.dp))

        statusMessage?.let { Text(it) }

        if (searchResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            searchResults.forEach { card ->
                Text(text = "${card.englishCard.orEmpty()} - ${card.vietnameseCard.orEmpty()}")
            }
        }
    }
}
