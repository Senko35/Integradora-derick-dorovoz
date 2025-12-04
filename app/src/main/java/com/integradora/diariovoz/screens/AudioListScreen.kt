package com.integradora.diariovoz.screens

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.integradora.diariovoz.viewmodel.AudioListViewModel
import com.integradora.diariovoz.data.AudioEntity
import java.text.SimpleDateFormat
import java.util.*
import com.integradora.diariovoz.ui.DoroButton

@Composable
fun AudioListScreen(
    navController: NavController? = null,
    viewModel: AudioListViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadAudios(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE4EC)) //
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        DoroButton(
            text = "⬅ Regresar",
            onClick = { navController?.navigate("record") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Grabaciones Guardadas",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFAD1457) // rosa fuerte
        )

        Spacer(modifier = Modifier.height(20.dp))

        state.audios.forEach { audio ->
            AudioItem(
                audio = audio,
                isCurrent = state.currentPlayingId == audio.id,
                isPlaying = state.isPlaying,
                progress = if (state.currentPlayingId == audio.id) state.progress else 0f,
                onTogglePlay = { viewModel.togglePlay(audio) },
                onSeek = { viewModel.seekTo(it) },
                onDelete = { viewModel.deleteAudio(context, audio) }
            )
        }
    }
}

@Composable
fun AudioItem(
    audio: AudioEntity,
    isCurrent: Boolean,
    isPlaying: Boolean,
    progress: Float,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // TÍTULO + FECHA
            Text(audio.fileName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = dateFormat.format(Date(audio.date)),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = onTogglePlay) {
                    Icon(
                        imageVector = if (isCurrent && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFFAD1457) // rosa fuerte
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Slider(
                    value = if (isCurrent) progress else 0f,
                    onValueChange = { newValue ->
                        if (isCurrent) onSeek(newValue)
                    },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFAD1457),
                        activeTrackColor = Color(0xFFD81B60)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = onDelete) {
                    Text("Eliminar", color = Color.Red)
                }
            }
        }
    }
}
