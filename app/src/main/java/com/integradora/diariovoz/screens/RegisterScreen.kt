package com.integradora.diariovoz.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.integradora.diariovoz.theme.PinkDark
import com.integradora.diariovoz.theme.PinkLight
import com.integradora.diariovoz.ui.DoroButton
import com.integradora.diariovoz.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegistroExitoso: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Crear Cuenta",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(30.dp))
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
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            )
        )

        Spacer(modifier = Modifier.height(18.dp))
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PinkDark,
                unfocusedBorderColor = PinkLight
            )
        )

        Spacer(modifier = Modifier.height(28.dp))
        DoroButton(
            text = if (state.isLoading) "Creando cuenta..." else "Registrar",
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!state.isLoading) {
                viewModel.register(
                    context = context,
                    name = name,
                    email = email,
                    pass = pass,
                    confirm = confirm
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        state.message?.let { msg ->
            Text(msg, style = MaterialTheme.typography.bodyMedium)
        }
        LaunchedEffect(state.success) {
            if (state.success) {
                onRegistroExitoso()
            }
        }
    }
}
