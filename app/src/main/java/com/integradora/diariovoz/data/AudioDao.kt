package com.integradora.diariovoz.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AudioDao {

    @Insert
    suspend fun insert(audio: AudioEntity)

    @Query("SELECT * FROM audios ORDER BY date DESC")
    suspend fun getAllAudios(): List<AudioEntity>

    @Delete
    suspend fun deleteAudio(audio: AudioEntity)

    @Query("SELECT * FROM audios WHERE userEmail = :email ORDER BY date DESC")
    suspend fun getAudiosByUser(email: String): List<AudioEntity>
}
