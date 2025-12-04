package com.integradora.diariovoz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audios")
data class AudioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val fileName: String,          // nombre visible
    val filePath: String,          // ruta completa
    val date: Long,                 // fecha (timestamp)
    val userEmail: String
)
