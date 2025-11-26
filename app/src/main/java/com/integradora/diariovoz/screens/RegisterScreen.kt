package com.integradora.diariovoz.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.integradora.diariovoz.theme.PinkDark
import com.integradora.diariovoz.theme.PinkLight
import com.integradora.diariovoz.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegistroExitoso: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Crear Cuenta", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(30.dp))

        // NOMBRE
        var name by remember { mutableStateOf("") }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // EMAIL
        var email by remember { mutableStateOf("") }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // CONTRASEÑA
        var pass by remember { mutableStateOf("") }
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // CONFIRMAR CONTRASEÑA
        var confirm by remember { mutableStateOf("") }
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // BOTÓN DE REGISTRO
        Button(
            onClick = {
                viewModel.register(
                    context = context,
                    name = name,
                    email = email,
                    pass = pass,
                    confirm = confirm
                )
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Registrar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MENSAJES DESDE EL VIEWMODEL
        state.message?.let { msg ->
            Text(msg, style = MaterialTheme.typography.bodyMedium)
        }

        // NAVEGAR AL LOGIN CUANDO SUCCESS = TRUE
        LaunchedEffect(state.success) {
            if (state.success) {
                onRegistroExitoso()
            }
        }
    }
}
