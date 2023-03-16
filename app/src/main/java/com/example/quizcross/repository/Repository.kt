package com.example.quizcross.repository

import com.example.quizcross.Retrofit.ClientRetrofit

class Repository {
    private val openTriviaApiService = ClientRetrofit.openTriviaApiService

    suspend fun getQuestion(category: Int, difficult: String) = ClientRetrofit.openTriviaApiService.getQuestion(category = category, difficulty = difficult)


}