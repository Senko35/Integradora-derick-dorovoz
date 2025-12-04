package com.integradora.diariovoz.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.integradora.diariovoz.data.AppDatabase
import com.integradora.diariovoz.data.User
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

    fun registrar(context: Context, email: String, pass: String) {

        if (email.isBlank() || pass.isBlank()) {
            showMessage("Por favor llena todos los campos.")
            return
        }

        viewModelScope.launch {

            val dao = AppDatabase.getInstance(context).userDao()

            // ¿Existe ya?
            val existingUser = dao.getUserByEmail(email)
            if (existingUser != null) {
                showMessage("El usuario ya existe.")
                return@launch
            }

            // Crear usuario
            val newUser = User(
                email = email,
                password = pass
            )

            dao.insert(newUser)

            _state.value = _state.value.copy(registerSuccess = true)
            showMessage("Usuario registrado correctamente.")
        }
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
            delay(500) // pequeña animación

            val dao = AppDatabase.getInstance(context).userDao()
            val user = dao.getUserByEmail(email)

            if (user != null && user.password == pass) {
                LoginSession.currentUserEmail = email
                showMessage("Inicio de sesión exitoso.")
                _state.value = _state.value.copy(
                    loginSuccess = true,
                    isPlaying = false
                )
            } else {
                showMessage("Credenciales incorrectas.")
            }

            _state.value = _state.value.copy(isLoading = false)
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
