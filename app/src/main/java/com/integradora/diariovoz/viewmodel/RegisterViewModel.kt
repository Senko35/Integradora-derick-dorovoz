package com.integradora.diariovoz.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.integradora.diariovoz.data.UserPreferences
import com.integradora.diariovoz.data.User
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

            val userPrefs = UserPreferences(context)

            if (userPrefs.userExists(email) || userPrefs.userExists(name)) {
                sendMessage("El correo o nombre ya está registrado")
                _state.value = _state.value.copy(isLoading = false)
                return@launch
            }

            userPrefs.saveUser(name, email, pass)

            // Registro exitoso
            _state.value = RegisterState(
                isLoading = false,
                message = "Registro exitoso",
                success = true
            )
        }
    }

    private fun sendMessage(msg: String) {
        _state.value = _state.value.copy(
            isLoading = false,
            message = msg
        )
    }
}
