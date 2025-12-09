package com.integradora.diariovoz.data.api

import com.integradora.diariovoz.data.AudioEntity
import com.integradora.diariovoz.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Modelos para Request/Response (DTOs)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val message: String, val name: String, val email: String)
data class RegisterResponse(val message: String, val user: User)
data class AudioRequest(val fileName: String, val filePath: String, val date: Long, val userEmail: String)
data class SimpleResponse(val message: String)

interface ApiService {
    
    @POST("register")
    suspend fun register(@Body user: User): Response<RegisterResponse>

    @POST("login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>

    @POST("audios")
    suspend fun saveAudio(@Body audio: AudioRequest): Response<SimpleResponse>

    @GET("audios/{userEmail}")
    suspend fun getAudios(@Path("userEmail") userEmail: String): Response<List<AudioRequest>>

    @DELETE("audios")
    suspend fun deleteAudio(
        @Query("fileName") fileName: String, 
        @Query("userEmail") userEmail: String
    ): Response<SimpleResponse>
}
