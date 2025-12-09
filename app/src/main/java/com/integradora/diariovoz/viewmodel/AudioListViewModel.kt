package com.integradora.diariovoz.viewmodel

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.integradora.diariovoz.data.AudioEntity
import com.integradora.diariovoz.data.api.AudioRenameRequest
import com.integradora.diariovoz.data.api.RetrofitClient
import com.integradora.diariovoz.session.LoginSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AudioListState(
    val audios: List<AudioEntity> = emptyList(),
    val currentPlayingId: Int? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val duration: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AudioListViewModel : ViewModel() {

    private val _state = MutableStateFlow(AudioListState())
    val state: StateFlow<AudioListState> = _state

    private var mediaPlayer: MediaPlayer? = null


    fun loadAudios(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val email = LoginSession.currentUserEmail
                if (email.isEmpty()) {
                    // Si no hay usuario logueado, no cargamos nada
                    _state.value = _state.value.copy(isLoading = false)
                    return@launch
                }

                val response = RetrofitClient.instance.getAudios(email)
                
                if (response.isSuccessful && response.body() != null) {
                    val apiList = response.body()!!
                    
                    // Mapeamos de AudioRequest (DTO) a AudioEntity (Modelo de UI)
                    // Usamos hashCode del nombre como ID temporal para la UI
                    val entityList = apiList.map { dto ->
                        AudioEntity(
                            id = dto.fileName.hashCode(),
                            fileName = dto.fileName,
                            filePath = dto.filePath,
                            date = dto.date,
                            userEmail = dto.userEmail
                        )
                    }
                    
                    _state.value = _state.value.copy(audios = entityList, isLoading = false)
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "Error al cargar audios")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Fallo de conexión: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    fun togglePlay(audio: AudioEntity) {
        if (_state.value.currentPlayingId == audio.id) {
            if (_state.value.isPlaying) {
                pause()
            } else {
                play()
            }
            return
        }

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer!!.setDataSource(audio.filePath)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

            _state.value = _state.value.copy(
                currentPlayingId = audio.id,
                isPlaying = true,
                duration = mediaPlayer!!.duration
            )

            startProgressUpdates()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun play() {
        mediaPlayer?.start()
        _state.value = _state.value.copy(isPlaying = true)
        startProgressUpdates()
    }


    private fun pause() {
        mediaPlayer?.pause()
        _state.value = _state.value.copy(isPlaying = false)
    }


    private fun startProgressUpdates() {
        viewModelScope.launch {
            while (_state.value.isPlaying && mediaPlayer != null) {

                val mp = mediaPlayer ?: break

                val progressValue = if (mp.duration > 0) {
                    mp.currentPosition.toFloat() / mp.duration.toFloat()
                } else 0f

                _state.value = _state.value.copy(progress = progressValue)

                delay(200)
            }
        }
    }


    fun seekTo(percent: Float) {
        val mp = mediaPlayer ?: return
        val pos = (mp.duration * percent).toInt()
        mp.seekTo(pos)
    }


    fun deleteAudio(context: Context, audio: AudioEntity) {
        viewModelScope.launch {
            // Si se elimina el audio que está sonando → detenerlo
            if (_state.value.currentPlayingId == audio.id) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null

                _state.value = _state.value.copy(
                    currentPlayingId = null,
                    isPlaying = false,
                    progress = 0f
                )
            }

            try {
                // Llamada a la API para eliminar
                val response = RetrofitClient.instance.deleteAudio(audio.fileName, audio.userEmail)
                
                if (response.isSuccessful) {
                    // Recargar lista desde el servidor
                    loadAudios(context)
                } else {
                    _state.value = _state.value.copy(error = "No se pudo eliminar el audio")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error de conexión al eliminar")
            }
        }
    }

    fun renameAudio(context: Context, audio: AudioEntity, newName: String) {
        if (newName.isBlank()) return

        viewModelScope.launch {
            try {
                val renameRequest = AudioRenameRequest(
                    userEmail = audio.userEmail,
                    currentFileName = audio.fileName,
                    newFileName = newName
                )

                val response = RetrofitClient.instance.renameAudio(renameRequest)

                if (response.isSuccessful) {
                    // Recargar lista para ver el cambio
                    loadAudios(context)
                } else {
                    _state.value = _state.value.copy(error = "No se pudo renombrar el audio")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error de conexión al renombrar")
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}
