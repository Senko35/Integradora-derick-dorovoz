package com.integradora.diariovoz.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.integradora.diariovoz.data.User
import com.integradora.diariovoz.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val success: Boolean = false
)

class RegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    fun register(context: Context, name: String, email: String, pass: String, confirm: String) {

        if (name.isBlank() || email.isBlank() || pass.isBlank() || confirm.isBlank()) {
            sendMessage("Completa todos los campos")
            return
        }

        if (pass != confirm) {
            sendMessage("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                // Crear objeto de usuario para el servidor
                val newUser = User(
                    name = name,
                    email = email,
                    password = pass
                )

                // LLamada a la API con Retrofit
                val response = RetrofitClient.instance.register(newUser)

                if (response.isSuccessful) {
                    _state.value = RegisterState(
                        isLoading = false,
                        message = "Registro exitoso",
                        success = true
                    )
                } else {
                    // El servidor devolvió un error (ej: usuario ya existe)
                    sendMessage("Error: ${response.code()} - Posiblemente el correo ya existe")
                }

            } catch (e: Exception) {
                // Error de conexión
                sendMessage("Error de conexión: ${e.message}")
            } finally {
                 // Asegurarnos de quitar el loading si no fue éxito
                 if(!_state.value.success){
                     _state.value = _state.value.copy(isLoading = false)
                 }
            }
        }
    }

    private fun sendMessage(msg: String) {
        _state.value = _state.value.copy(
            isLoading = false,
            message = msg
        )
    }
}
