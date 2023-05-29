package com.example.quizcross.repository

import com.example.quizcross.model.Question
import com.example.quizcross.model.ResultModel
import com.example.quizcross.retrofit.RetrofitClient
import com.example.quizcross.retrofit.ServiceRetrofit
import com.example.quizcross.service.APIListener

class QuestionRepository {
    private val remote = RetrofitClient.getServices(ServiceRetrofit::class.java)

    suspend fun loadQuestion(category: Int, difficult: String, listener: APIListener<Question>){
        try {
            val response = remote.getQuestion(1, category, difficult)
            if (response.isSuccessful) {
                val questionResponse = response.body()
                if (questionResponse != null && questionResponse.results.isNotEmpty()) {
                    val question = questionResponse.results[0]
                    listener.onSuccess(ResultModel.Success(question))
                } else {
                    listener.onFailure(response.errorBody().toString())
                }
            } else {
                listener.onFailure(response.errorBody().toString())
            }
        } catch (e: Exception) {
            listener.onFailure(e.message.toString())
        }

    }
}