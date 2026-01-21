package com.example.annam

import android.util.Log
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchCard(
    navBack: () -> Unit,
    navigateToResults: (SearchCardsRoute) -> Unit
) {
    var enWord by remember { mutableStateOf("") }
    var vnWord by remember { mutableStateOf("") }
    var isCheckedEnglish by remember { mutableStateOf(false) }
    var isCheckedVietnamese by remember { mutableStateOf(false) }

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
                label = { Text("vn") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val englishQuery = enWord.trim()
                val vietnameseQuery = vnWord.trim()
                Log.d("search query", "Search for: $englishQuery / $vietnameseQuery")
                navigateToResults(
                    SearchCardsRoute(
                        en = englishQuery,
                        vn = vietnameseQuery,
                        searchExactByEnglish = isCheckedEnglish,
                        searchExactByVietnamese = isCheckedVietnamese
                    )
                )
            }
        ) {
            Text("Search")
        }

    }
}
