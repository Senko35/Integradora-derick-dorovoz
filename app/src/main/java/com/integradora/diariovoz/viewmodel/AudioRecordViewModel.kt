package com.integradora.diariovoz.viewmodel

import android.content.Context
import android.media.MediaRecorder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.integradora.diariovoz.data.api.AudioRequest
import com.integradora.diariovoz.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

data class AudioRecordState(
    val isRecording: Boolean = false,
    val filePath: String? = null,
    val audioSaved: Boolean = false
)

class AudioRecordViewModel : ViewModel() {

    private val _state = MutableStateFlow(AudioRecordState())
    val state: StateFlow<AudioRecordState> = _state

    private var recorder: MediaRecorder? = null


    fun startRecording(outputFile: File) {
        stopRecording()

        try {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)

                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            _state.value = AudioRecordState(
                isRecording = true,
                filePath = outputFile.absolutePath,
                audioSaved = false
            )

        } catch (e: Exception) {
            _state.value = AudioRecordState(
                isRecording = false,
                filePath = null,
                audioSaved = false
            )
            e.printStackTrace()
        }
    }

    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (_: Exception) {

        }

        recorder = null

        _state.value = _state.value.copy(
            isRecording = false
        )
    }

    fun saveAudio(context: Context, userEmail: String) {
        val path = _state.value.filePath ?: return
        val file = File(path)

        if (!file.exists()) return

        viewModelScope.launch {
            try {
                // Preparar objeto para enviar
                val audioRequest = AudioRequest(
                    fileName = file.name,
                    filePath = file.absolutePath,
                    date = System.currentTimeMillis(),
                    userEmail = userEmail
                )

                // Llamada a Retrofit
                val response = RetrofitClient.instance.saveAudio(audioRequest)

                if (response.isSuccessful) {
                     _state.value = _state.value.copy(audioSaved = true)
                } else {
                    // Manejar error (opcional: mostrar mensaje)
                    println("Error guardando audio: ${response.code()}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        stopRecording()
    }
}
