package com.example.annam

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun StudyCard(
    navBack: () -> Unit,
    flashCardDao: FlashCardDao
) {
    var lesson by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableStateOf(0) }
    var showingEnglish by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val pulledLesson = withContext(Dispatchers.IO) {
            flashCardDao.loadRandomThree()
        }
        lesson = pulledLesson
        isLoading = false
        currentIndex = 0
        showingEnglish = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.align(Alignment.Start),
            onClick = navBack
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Text(text = "Loading lesson...")
            }
            lesson.isEmpty() -> {
                Text(text = "No flashcards available to study.")
            }
            else -> {
                val currentCard = lesson[currentIndex]
                val displayWord = if (showingEnglish) {
                    currentCard.englishCard.orEmpty()
                } else {
                    currentCard.vietnameseCard.orEmpty()
                }

                Text(
                    text = displayWord,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        showingEnglish = !showingEnglish
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!showingEnglish) {
                    Button(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        onClick = {
                            if (lesson.isNotEmpty()) {
                                currentIndex = (currentIndex + 1) % lesson.size
                                showingEnglish = true
                            }
                        }
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}
