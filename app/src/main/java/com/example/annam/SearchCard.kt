package com.example.annam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
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
    navigateToResults: (SearchResultsRoute) -> Unit
) {
    var enWord by remember { mutableStateOf("") }
    var vnWord by remember { mutableStateOf("") }
    var isCheckedEnglish by remember { mutableStateOf(false) }
    var isCheckedVietnamese by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier.fillMaxWidth()
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            modifier = Modifier.align(Alignment.Start),
            onClick = navBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isCheckedEnglish,
                onCheckedChange = { isCheckedEnglish = it }
            )

            TextField(
                value = enWord,
                onValueChange = { enWord = it },
                label = { Text("en") },
                modifier = Modifier.weight(1f)
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
                label = { Text("vn") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val englishQuery = enWord.trim()
                val vietnameseQuery = vnWord.trim()
                navigateToResults(
                    SearchResultsRoute(
                        en = englishQuery,
                        vn = vietnameseQuery,
                        exactByEnglish = isCheckedEnglish,
                        exactByVietnamese = isCheckedVietnamese
                    )
                )
            }
        ) {
            Text("Search",
                )
        }
    }
    }



