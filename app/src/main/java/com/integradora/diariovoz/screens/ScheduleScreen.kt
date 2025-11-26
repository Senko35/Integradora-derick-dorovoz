package com.integradora.diariovoz.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScheduleScreen(audioPath: String) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "📅 Agenda")
        Spacer(modifier = Modifier.height(20.dp))

        if (audioPath.isNotEmpty()) {
            Text(text = "Audio recibido:")
            Text(text = audioPath)
        } else {
            Text(text = "No se recibió archivo de audio.")
        }
    }
}
