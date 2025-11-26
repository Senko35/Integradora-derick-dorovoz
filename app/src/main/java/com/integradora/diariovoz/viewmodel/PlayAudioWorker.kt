package com.integradora.diariovoz.viewmodel

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.work.Worker
import androidx.work.WorkerParameters

class PlayAudioWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        val path = inputData.getString("audio_path") ?: return Result.failure()

        val player = ExoPlayer.Builder(applicationContext).build()
        val mediaItem = MediaItem.fromUri("file://$path")

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        Thread.sleep(10_000)  // dejar sonar el audio 10 seg

        player.stop()
        player.release()

        return Result.success()
    }
}
