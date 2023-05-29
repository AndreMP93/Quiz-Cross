package com.example.quizcross.model

import com.google.gson.annotations.SerializedName
class QuestionResponse(){
    @SerializedName("response_code")
    var responseCode: Int = 0
    @SerializedName("results")
    var results: List<Question> = mutableListOf()

}

