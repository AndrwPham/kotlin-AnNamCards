package com.example.annam

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun StudyCard(
    navBack: () -> Unit,
    flashCardDao: FlashCardDao,
    networkService: NetworkService
) {
    var lesson by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableStateOf(0) }
    var showingEnglish by remember { mutableStateOf(true) }
    var isGenerating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasAudio by remember { mutableStateOf(false) }
    var player: ExoPlayer? by remember { mutableStateOf(null) }
    var playbackStatus by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val appContext = context.applicationContext
    val filesDir = appContext.filesDir

    LaunchedEffect(Unit) {
        val pulledLesson = withContext(Dispatchers.IO) { flashCardDao.loadRandomThree() }
        lesson = pulledLesson
        isLoading = false
        currentIndex = 0
        showingEnglish = true
        hasAudio = pulledLesson.firstOrNull()?.vietnameseCard?.let { audioFile(filesDir, it).exists() } == true
    }

    LaunchedEffect(currentIndex, lesson) {
        val word = lesson.getOrNull(currentIndex)?.vietnameseCard
        hasAudio = word?.let { audioFile(filesDir, it).exists() } == true
    }

    DisposableEffect(Unit) {
        onDispose { player?.release() }
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
            isLoading -> Text(text = "Loading lesson...")
            lesson.isEmpty() -> Text(text = "No flashcards available to study.")
            else -> {
                val currentCard = lesson[currentIndex]
                val displayWord = if (showingEnglish) currentCard.englishCard.orEmpty() else currentCard.vietnameseCard.orEmpty()

                Text(
                    text = displayWord,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showingEnglish = !showingEnglish }
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
                    ) { Text("Next") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val currentWord = currentCard.vietnameseCard.orEmpty()
                if (currentWord.isNotEmpty()) {
                    if (!showingEnglish && hasAudio) {
                        Button(
                            modifier = Modifier.fillMaxWidth(0.6f),
                            onClick = {
                                val file = audioFile(filesDir, currentWord)
                                if (!file.exists()) {
                                    hasAudio = false
                                    return@Button
                                }
                                playbackStatus = "Loading..."
                                player?.release()
                                val mediaItem = MediaItem.fromUri(file.toUri())
                                val newPlayer = ExoPlayer.Builder(appContext).build().apply {
                                    setMediaItem(mediaItem)
                                    addListener(object : Player.Listener {
                                        override fun onPlaybackStateChanged(state: Int) {
                                            playbackStatus = when (state) {
                                                Player.STATE_BUFFERING -> "Buffering..."
                                                Player.STATE_READY -> "Ready"
                                                Player.STATE_ENDED -> "Finished"
                                                else -> ""
                                            }
                                        }
                                    })
                                    prepare()
                                    playWhenReady = true
                                }
                                player = newPlayer
                            }
                        ) { Text("Play") }
                    } else if (!showingEnglish) {
                        Button(
                            enabled = !isGenerating,
                            modifier = Modifier.fillMaxWidth(0.6f),
                            onClick = {
                                isGenerating = true
                                errorMessage = null
                                playbackStatus = ""
                                scope.launch {
                                    try {
                                        val prefs = withContext(Dispatchers.IO) { appContext.dataStore.data.first() }
                                        val email = prefs[EMAIL].orEmpty()
                                        val token = prefs[TOKEN].orEmpty()
                                        Log.d("demo", "Using credentials email=$email token=$token")
                                        if (email.isBlank() || token.isBlank()) {
                                            errorMessage = "Missing email or token"
                                            return@launch
                                        }
                                        val response = withContext(Dispatchers.IO) {
                                            networkService.generateAudio(
                                                request = AudioRequest(
                                                    word = currentWord,
                                                    email = email,
                                                    token = token
                                                )
                                            )
                                        }
                                        Log.d("AudioResponse",response.toString())
                                        if (response.code == 200) {
                                            val bytes = Base64.decode(response.message, Base64.DEFAULT)
                                            saveAudioToInternalStorage(appContext, bytes, audioFileName(currentWord))
                                            hasAudio = true
                                        } else {
                                            errorMessage = response.message
                                        }
                                    } catch (e: Exception) {
                                        Log.d("demo", "Audio generation failed: $e")
                                        errorMessage = "Error: ${e.message}"
                                    } finally {
                                        isGenerating = false
                                    }
                                }
                            }
                        ) { Text(if (isGenerating) "Generating..." else "Generate audio") }
                    }
                }

                if (playbackStatus.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = playbackStatus)
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it)
                }
            }
        }
    }
}

private fun audioFileName(word: String): String {
    val safeName = word.lowercase().replace(Regex("[^a-z0-9]+"), "_").ifBlank { "audio" }
    return "$safeName.mp3"
}

private fun audioFile(dir: File, word: String): File = File(dir, audioFileName(word))

private fun saveAudioToInternalStorage(context: Context, audioData: ByteArray, filename: String) {
    val file = File(context.filesDir, filename)
    FileOutputStream(file).use { fos ->
        fos.write(audioData)
    }
}
