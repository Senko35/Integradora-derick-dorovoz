package com.integradora.diariovoz.screens

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.integradora.diariovoz.R
import com.integradora.diariovoz.viewmodel.LoginViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.integradora.diariovoz.theme.PinkDark
import com.integradora.diariovoz.theme.PinkLight

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onRegistrarClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // -------------------------------
    // CONFIGURAR EXOPLAYER (NO SE MODIFICÓ)
    // -------------------------------
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem =
                MediaItem.fromUri("android.resource://${context.packageName}/raw/doroowo")
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
            playWhenReady = true
            volume = if (state.isMuted) 0f else 0.4f
        }
    }

    // Mute / Unmute dinámico
    LaunchedEffect(state.isMuted) {
        exoPlayer.volume = if (state.isMuted) 0f else 0.4f
    }

    // Reproducir o pausar si VM lo pide
    LaunchedEffect(state.isPlaying) {
        exoPlayer.playWhenReady = state.isPlaying
    }

    // Iniciar música al entrar
    LaunchedEffect(Unit) {
        viewModel.setPlaying(true)
    }

    // Liberar player al salir
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    // Navegación al iniciar sesión
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            exoPlayer.stop()
            exoPlayer.release()
            viewModel.clearAuthFlags()
            onLoginSuccess()
        }
    }

    // -------------------------------------
    //               UI
    // -------------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // LOGO
        val logoResId = try {
            R.drawable.doro
        } catch (e: Resources.NotFoundException) {
            0
        }

        if (logoResId != 0) {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text("Bienvenido", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(20.dp))

        // NOMBRE
        OutlinedTextField(
            value = state.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = { Text("Nombre") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // CONTRASEÑA
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(22.dp))

        // -----------------------
        // BOTÓN LOGIN + MUTE
        // -----------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    // **CORREGIDO**
                    viewModel.login(context)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Login, contentDescription = "login")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Sesión")
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(onClick = { viewModel.toggleMute() }) {
                if (state.isMuted) {
                    Icon(Icons.Default.VolumeOff, contentDescription = "Unmute")
                } else {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Mute")
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        TextButton(onClick = onRegistrarClick) {
            Text("Registrarse")
        }

        // Mensajes desde ViewModel
        state.message?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(msg, style = MaterialTheme.typography.bodyMedium)
            LaunchedEffect(msg) { viewModel.clearMessage() }
        }
    }
}
