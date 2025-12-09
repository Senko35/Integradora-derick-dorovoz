package com.integradora.diariovoz.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Para emulador Android usa: "http://10.0.2.2:8000/"
    // Para dispositivo físico usa tu IP local: "http://192.168.100.140:8000/"
    // IMPORTANTE: Asegurate de usar http (no https) si no tienes certificados configurados
    private const val BASE_URL = "http://192.168.100.140:8000/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
