package com.integradora.diariovoz.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.integradora.diariovoz.data.api.LoginRequest
import com.integradora.diariovoz.data.api.RetrofitClient
import com.integradora.diariovoz.session.LoginSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginState(
    val nombre: String = "",
    val password: String = "",
    val isMuted: Boolean = false,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state
    fun onNombreChange(newValue: String) {
        _state.value = _state.value.copy(nombre = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _state.value = _state.value.copy(password = newValue)
    }
    fun toggleMute() {
        _state.value = _state.value.copy(isMuted = !_state.value.isMuted)
    }

    fun setPlaying(isPlaying: Boolean) {
        _state.value = _state.value.copy(isPlaying = isPlaying)
    }

    fun login(context: Context) {
        val email = _state.value.nombre
        val pass = _state.value.password

        if (email.isBlank() || pass.isBlank()) {
            showMessage("Por favor llena todos los campos.")
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)
            // delay(500) // Animación (opcional si la red es rápida)

            try {
                // Crear objeto de petición
                val loginRequest = LoginRequest(email = email, password = pass)

                // Llamada a Retrofit
                val response = RetrofitClient.instance.login(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    
                    // Guardar sesión
                    LoginSession.currentUserEmail = body.email
                    // Podrías guardar también el nombre: body.name
                    
                    showMessage("Inicio de sesión exitoso. Hola ${body.name}")
                    _state.value = _state.value.copy(
                        loginSuccess = true,
                        isPlaying = false
                    )
                } else {
                    // Error credenciales (401) u otro
                    showMessage("Credenciales incorrectas o error en servidor.")
                }

            } catch (e: Exception) {
                showMessage("Error de conexión: ${e.message}")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
    private fun showMessage(msg: String) {
        _state.value = _state.value.copy(message = msg)
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    fun clearAuthFlags() {
        _state.value = _state.value.copy(
            loginSuccess = false,
            registerSuccess = false
        )
    }
}
