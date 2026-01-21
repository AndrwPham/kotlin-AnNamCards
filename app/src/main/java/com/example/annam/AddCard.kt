package com.example.annam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.ui.unit.sp
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

    Column (
        modifier = Modifier.fillMaxWidth()
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
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
            label = { Text("en") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = vnWord,
            onValueChange = { vnWord = it },
            label = { Text("vn") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

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
                // thử lưu DB -> nếu lỗi thì thôi, quay về màn hình chính, xoá chữ
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
