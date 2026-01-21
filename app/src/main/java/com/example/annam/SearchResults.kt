package com.example.annam

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchResults(
    navBack: () -> Unit,
    flashCardDao: FlashCardDao,
    englishQuery: String,
    vietnameseQuery: String,
    englishExact: Boolean,
    vietnameseExact: Boolean,
    navigateToEdit: (EditCardRoute) -> Unit
) {
    var results by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val actionColumnWidth = 200.dp

    LaunchedEffect(englishQuery, vietnameseQuery, englishExact, vietnameseExact) {
        isLoading = true
        statusMessage = null
        try {
            Log.d(
                "SearchQuery",
                "en='$englishQuery' exactEn=$englishExact vn='$vietnameseQuery' exactVn=$vietnameseExact"
            )
            val matches = withContext(Dispatchers.IO) {
                flashCardDao.getFilteredFlashCards(
                    en = englishQuery,
                    exactEn = if (englishExact) 1 else 0,
                    vn = vietnameseQuery,
                    exactVn = if (vietnameseExact) 1 else 0
                )
            }
            results = matches
            statusMessage = if (matches.isEmpty()) "No matches found." else null
        } catch (e: Exception) {
            statusMessage = "Search failed: ${e.message}"
            results = emptyList()
        } finally {
            isLoading = false
        }
    }

    Column {
        Row()
        {
            Button(onClick = navBack) { Text("Back") }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Search Results")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> Text("Searching...")
            statusMessage != null -> Text(statusMessage!!)
        }

        if (results.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "English",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Vietnamese",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Actions",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(actionColumnWidth)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            results.forEach { card ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = card.englishCard.orEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = card.vietnameseCard.orEmpty(),
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        modifier = Modifier.width(actionColumnWidth),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            navigateToEdit(EditCardRoute(
                                english = card.englishCard.orEmpty(),
                                vietnamese = card.vietnameseCard.orEmpty()))
                        }) {
                            Text("Edit")
                        }
                        Button(onClick = {
                            scope.launch {
                                try {
                                    withContext(Dispatchers.IO) {
                                        flashCardDao.delete(card)
                                    }
                                    val updated = results.filter { it.uid != card.uid }
                                    results = updated
                                    statusMessage = if (updated.isEmpty()) "No matches found." else null
                                } catch (e: Exception) {
                                    statusMessage = "Delete failed: ${e.message}"
                                }
                            }
                        }) {
                            Text("Delete")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
