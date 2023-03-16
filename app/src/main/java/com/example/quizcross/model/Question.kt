package com.example.quizcross.model

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("category")
    var category: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("difficulty")
    var difficulty: String,
    @SerializedName("question")
    var quetion: String,
    @SerializedName("correct_answer")
    var correct_answer: String,
    @SerializedName("incorrect_answers")
    var incorrect_answers: List<String>
)
