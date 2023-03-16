package com.example.quizcross.model

data class QuestionResponse (
    val responseCode: Int,
    val results: List<Question>
)