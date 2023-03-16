package com.example.quizcross.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClientRetrofit {
    private const val BASE_URL = "https://opentdb.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val openTriviaApiService: ServiceRetrofit by lazy {
        retrofit.create(ServiceRetrofit::class.java)
    }
}