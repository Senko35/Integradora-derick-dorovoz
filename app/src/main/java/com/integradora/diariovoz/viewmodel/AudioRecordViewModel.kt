package com.integradora.diariovoz.viewmodel

import android.media.MediaRecorder
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

data class AudioRecordState(
    val isRecording: Boolean = false,
    val filePath: String? = null
)

class AudioRecordViewModel : ViewModel() {

    private val _state = MutableStateFlow(AudioRecordState())
    val state: StateFlow<AudioRecordState> = _state

    private var recorder: MediaRecorder? = null

    // -----------------------------
    // INICIAR GRABACIÓN
    // -----------------------------
    fun startRecording(outputFile: File) {
        stopRecording() // por si había algo previo

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
                filePath = outputFile.absolutePath
            )

        } catch (e: Exception) {
            _state.value = AudioRecordState(
                isRecording = false,
                filePath = null
            )
            e.printStackTrace()
        }
    }

    // -----------------------------
    // DETENER GRABACIÓN
    // -----------------------------
    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (_: Exception) {
            // Si se intenta detener sin grabar, ignoramos error
        }

        recorder = null

        _state.value = _state.value.copy(
            isRecording = false
        )
    }

    // Se ejecuta cuando Compose destruye la pantalla
    override fun onCleared() {
        super.onCleared()
        stopRecording()
    }
}
