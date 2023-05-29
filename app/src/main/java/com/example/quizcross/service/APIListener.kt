package com.example.quizcross.service

import com.example.quizcross.model.ResultModel

interface APIListener<T : Any> {
    fun onSuccess(result: ResultModel.Success<T>)
    fun onFailure(onMessage: String)
}