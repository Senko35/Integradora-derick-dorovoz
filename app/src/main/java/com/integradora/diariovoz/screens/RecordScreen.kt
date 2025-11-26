package com.integradora.diariovoz.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.integradora.diariovoz.viewmodel.AudioRecordViewModel
import java.io.File

@Composable
fun RecordScreen(
    viewModel: AudioRecordViewModel = viewModel(),
    onGoToSchedule: (String) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val outputDir = context.filesDir
    val audioFile = remember {
        File(outputDir, "audio_${System.currentTimeMillis()}.m4a")
    }

    // Verificar permiso
    val hasPermission = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) {
        Text("La app necesita permiso para usar el micrófono.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // -------------------------
        // BOTÓN DE GRABACIÓN
        // -------------------------
        if (!state.isRecording) {
            Button(
                onClick = { viewModel.startRecording(audioFile) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎙️ Iniciar grabación")
            }
        } else {
            Button(
                onClick = { viewModel.stopRecording() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏹️ Detener grabación")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (state.filePath != null && !state.isRecording) {
            Text("Archivo guardado en:")
            Text(state.filePath!!)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // -------------------------
        // IR A SCHEDULE
        // -------------------------
        if (state.filePath != null && !state.isRecording) {
            Button(
                onClick = { onGoToSchedule(state.filePath!!) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏭️ Continuar a Agenda")
            }
        }
    }
}
