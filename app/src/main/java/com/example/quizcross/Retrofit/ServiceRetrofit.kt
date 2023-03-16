package com.example.quizcross.Retrofit

import com.example.quizcross.model.Question
import com.example.quizcross.model.QuestionResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Dictionary
import java.util.Objects

interface ServiceRetrofit {
    //https://opentdb.com/api.php?amount=1&category=9&difficulty=medium&type=multiple
    @GET("api.php")
    suspend fun getQuestion(
        @Query("amount") amount: Int = 1,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String = "multiple"
    ): QuestionResponse
}
