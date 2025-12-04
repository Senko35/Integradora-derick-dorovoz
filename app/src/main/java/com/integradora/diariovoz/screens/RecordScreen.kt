package com.integradora.diariovoz.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.integradora.diariovoz.R
import com.integradora.diariovoz.session.LoginSession
import com.integradora.diariovoz.theme.PinkDark
import com.integradora.diariovoz.theme.PinkLight
import com.integradora.diariovoz.ui.DoroButton
import com.integradora.diariovoz.viewmodel.AudioRecordViewModel
import java.io.File

@Composable
fun RecordScreen(
    viewModel: AudioRecordViewModel = viewModel(),
    onGoToSchedule: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    var audioName by remember { mutableStateOf("") }
    var hasPermission by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    if (!hasPermission) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Solicitando permiso para usar el micrófono...")
        }
        return
    }

    val audioFile = remember(audioName) {
        File(
            context.filesDir,
            if (audioName.isBlank()) "audio_${System.currentTimeMillis()}.m4a"
            else "$audioName.m4a"
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.doro),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.18f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // ⭐ CENTRADO
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                DoroButton(
                    text = " Mis grabaciones",
                    modifier = Modifier.weight(1f)
                ) { onGoToSchedule() }

                Spacer(modifier = Modifier.width(10.dp))

                DoroButton(
                    text = "Cerrar sesión",
                    modifier = Modifier.weight(1f)
                ) {
                    LoginSession.currentUserEmail = ""
                    onLogout()
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            if (!state.isRecording && state.filePath == null) {
                OutlinedTextField(
                    value = audioName,
                    onValueChange = { audioName = it },
                    label = { Text("Nombre del audio") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkDark,
                        unfocusedBorderColor = PinkLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (!state.isRecording) {
                DoroButton(
                    text = "🎙️ Iniciar grabación",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (audioName.isNotBlank()) {
                        viewModel.startRecording(audioFile)
                    }
                }
            } else {
                DoroButton(
                    text = "⏹️ Detener grabación",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel.stopRecording()
                    val email = LoginSession.currentUserEmail
                    viewModel.saveAudio(context, email)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (state.audioSaved) {
                Text("✔ Audio guardado", color = PinkDark)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
