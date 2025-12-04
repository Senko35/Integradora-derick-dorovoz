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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.integradora.diariovoz.R
import com.integradora.diariovoz.viewmodel.LoginViewModel
import com.integradora.diariovoz.theme.PinkDark
import com.integradora.diariovoz.theme.PinkLight
import com.integradora.diariovoz.ui.DoroButton
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onRegistrarClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
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

    LaunchedEffect(state.isMuted) {
        exoPlayer.volume = if (state.isMuted) 0f else 0.4f
    }

    LaunchedEffect(state.isPlaying) {
        exoPlayer.playWhenReady = state.isPlaying
    }

    LaunchedEffect(Unit) {
        viewModel.setPlaying(true)
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {

            try {
                exoPlayer.stop()
                exoPlayer.release()
            } catch (_: Exception) {}

            delay(200)

            viewModel.clearAuthFlags()
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

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

        OutlinedTextField(
            value = state.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = { Text("Correo") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            ),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DoroButton(
                text = "Iniciar Sesión",
                modifier = Modifier.weight(1f)
            ) {
                viewModel.login(context)
            }

            Spacer(modifier = Modifier.width(14.dp))

            IconButton(onClick = { viewModel.toggleMute() }) {
                if (state.isMuted)
                    Icon(Icons.Default.VolumeOff, contentDescription = "Unmute")
                else
                    Icon(Icons.Default.VolumeUp, contentDescription = "Mute")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = onRegistrarClick) {
            Text("Registrarse")
        }

        state.message?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(msg, style = MaterialTheme.typography.bodyMedium)
            LaunchedEffect(msg) { viewModel.clearMessage() }
        }
    }
}
