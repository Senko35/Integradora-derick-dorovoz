package com.integradora.diariovoz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AudioSchedulerViewModel : ViewModel() {

    fun scheduleAudio(path: String, calendar: Calendar) {

        val now = Calendar.getInstance().timeInMillis
        val due = calendar.timeInMillis

        val delay = due - now
        if (delay <= 0) return

        val data = workDataOf("audio_path" to path)

        val request = OneTimeWorkRequestBuilder<PlayAudioWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance().enqueue(request)
    }
}
