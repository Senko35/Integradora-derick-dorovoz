package com.integradora.diariovoz.viewmodel

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.integradora.diariovoz.data.AppDatabase
import com.integradora.diariovoz.data.AudioEntity
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
    val duration: Int = 0
)

class AudioListViewModel : ViewModel() {

    private val _state = MutableStateFlow(AudioListState())
    val state: StateFlow<AudioListState> = _state

    private var mediaPlayer: MediaPlayer? = null


    fun loadAudios(context: Context) {
        viewModelScope.launch {
            val dao = AppDatabase.getInstance(context).audioDao()
            val list = dao.getAudiosByUser(LoginSession.currentUserEmail)
            _state.value = _state.value.copy(audios = list)
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


            val dao = AppDatabase.getInstance(context).audioDao()
            dao.deleteAudio(audio)

            // Recargar lista
            val list = dao.getAudiosByUser(LoginSession.currentUserEmail)
            _state.value = _state.value.copy(audios = list)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}
