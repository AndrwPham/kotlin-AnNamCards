package com.example.annam

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
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
fun EditCard(
    navBack: () -> Unit,
    flashCardDao: FlashCardDao,
    english: String,
    vietnamese: String,
    networkService: NetworkService
) {
    var enWord by remember { mutableStateOf("") }
    var vnWord by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var cardUid by remember { mutableStateOf<Int?>(null) }
    var hasAudio by remember { mutableStateOf(false) }
    var audioFileLabel by remember { mutableStateOf<String?>(null) }
    var isGeneratingAudio by remember { mutableStateOf(false) }
    var isDeletingAudio by remember { mutableStateOf(false) }
    var playbackStatus by remember { mutableStateOf("") }
    var audioMessage by remember { mutableStateOf<String?>(null) }
    var player: ExoPlayer? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val appContext = context.applicationContext
    val filesDir = appContext.filesDir

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

    LaunchedEffect(vnWord) {
        val word = vnWord.trim()
        if (word.isBlank()) {
            hasAudio = false
            audioFileLabel = null
            return@LaunchedEffect
        }
        val file = audioFile(filesDir, word)
        hasAudio = file.exists()
        audioFileLabel = if (hasAudio) file.name else null
    }

    DisposableEffect(Unit) {
        onDispose { player?.release() }
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
                            }
                        }
                    }
                ) {
                    Text(if (isSaving) "Updating..." else "Update")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = audioFileLabel.orEmpty(),
                    onValueChange = {},
                    label = { Text("audio file") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (hasAudio) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val word = vnWord.trim()
                                if (word.isBlank()) return@Button
                                val file = audioFile(filesDir, word)
                                if (!file.exists()) {
                                    hasAudio = false
                                    audioFileLabel = null
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
                        ) {
                            Text("Play")
                        }
                        Button(
                            enabled = !isDeletingAudio,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val word = vnWord.trim()
                                if (word.isBlank()) return@Button
                                isDeletingAudio = true
                                audioMessage = null
                                scope.launch {
                                    try {
                                        val deleted = withContext(Dispatchers.IO) {
                                            audioFile(filesDir, word).delete()
                                        }
                                        if (!deleted) {
                                            audioMessage = "Delete failed."
                                        }
                                    } catch (e: Exception) {
                                        audioMessage = "Delete failed: ${e.message}"
                                    } finally {
                                        isDeletingAudio = false
                                        val file = audioFile(filesDir, word)
                                        hasAudio = file.exists()
                                        audioFileLabel = if (hasAudio) file.name else null
                                    }
                                }
                            }
                        ) {
                            Text(if (isDeletingAudio) "Deleting..." else "Delete")
                        }
                    }
                } else {
                    Button(
                        enabled = !isGeneratingAudio,
                        modifier = Modifier.fillMaxWidth(0.7f),
                        onClick = {
                            val word = vnWord.trim()
                            if (word.isBlank()) {
                                audioMessage = "Vietnamese word is required."
                                return@Button
                            }
                            isGeneratingAudio = true
                            audioMessage = null
                            playbackStatus = ""
                            scope.launch {
                                try {
                                    val prefs = withContext(Dispatchers.IO) { appContext.dataStore.data.first() }
                                    val email = prefs[EMAIL].orEmpty()
                                    val token = prefs[TOKEN].orEmpty()
                                    if (email.isBlank() || token.isBlank()) {
                                        audioMessage = "Missing email or token"
                                        return@launch
                                    }
                                    val response = withContext(Dispatchers.IO) {
                                        networkService.generateAudio(
                                            request = AudioRequest(
                                                word = word,
                                                email = email,
                                                token = token
                                            )
                                        )
                                    }
                                    if (response.code == 200) {
                                        val bytes = Base64.decode(response.message, Base64.DEFAULT)
                                        saveAudioToInternalStorage(appContext, bytes, audioFileName(word))
                                        hasAudio = true
                                        audioFileLabel = audioFileName(word)
                                    } else {
                                        audioMessage = response.message
                                    }
                                } catch (e: Exception) {
                                    audioMessage = "Error: ${e.message}"
                                } finally {
                                    isGeneratingAudio = false
                                }
                            }
                        }
                    ) { Text(if (isGeneratingAudio) "Generating..." else "Generate audio") }
                }

                if (playbackStatus.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = playbackStatus)
                }

                audioMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it)
                }

                statusMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it)
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
