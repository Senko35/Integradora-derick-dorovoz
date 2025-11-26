package com.integradora.diariovoz.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.integradora.diariovoz.data.AppDatabase
import com.integradora.diariovoz.data.User
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

    // --------------------------
    // INPUT (NO TOCAR)
    // --------------------------
    fun onNombreChange(newValue: String) {
        _state.value = _state.value.copy(nombre = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _state.value = _state.value.copy(password = newValue)
    }

    // --------------------------
    // AUDIO (NO TOCAR)
    // --------------------------
    fun toggleMute() {
        _state.value = _state.value.copy(isMuted = !_state.value.isMuted)
    }

    fun setPlaying(isPlaying: Boolean) {
        _state.value = _state.value.copy(isPlaying = isPlaying)
    }

    // --------------------------
    // REGISTRAR → ahora guarda en ROOM
    // --------------------------
    fun registrar(context: Context, nameOrEmail: String, pass: String) {
        if (nameOrEmail.isBlank() || pass.isBlank()) {
            showMessage("Por favor llena todos los campos.")
            return
        }

        viewModelScope.launch {
            val dao = AppDatabase.getDatabase(context).userDao()

            val exists = dao.getUserByEmail(nameOrEmail)
            if (exists != null) {
                showMessage("El usuario ya existe.")
                return@launch
            }

            dao.insert(User(name = nameOrEmail, email = nameOrEmail, password = pass))

            _state.value = _state.value.copy(registerSuccess = true)
            showMessage("Usuario registrado correctamente.")
        }
    }

    // --------------------------
    // LOGIN → ahora valida en ROOM
    // --------------------------
    fun login(context: Context) {
        val name = state.value.nombre
        val pass = state.value.password

        if (name.isBlank() || pass.isBlank()) {
            showMessage("Por favor llena todos los campos.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            delay(800)

            val dao = AppDatabase.getDatabase(context).userDao()
            val user = dao.getUserByEmail(name)

            if (user != null && user.password == pass) {
                showMessage("Inicio de sesión exitoso.")
                _state.value = _state.value.copy(
                    isPlaying = false,
                    loginSuccess = true
                )
            } else {
                showMessage("Credenciales incorrectas.")
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    // --------------------------
    // UTILES (NO TOCAR)
    // --------------------------
    private fun showMessage(msg: String) {
        _state.value = _state.value.copy(message = msg)
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    fun clearAuthFlags() {
        _state.value = _state.value.copy(loginSuccess = false, registerSuccess = false)
    }
}
