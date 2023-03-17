package com.example.quizcross.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.repository.QuestionRepository
import com.example.quizcross.viewmodel.QuestionViewModel


class QuestionViewModelFactory(private val questionRepository: QuestionRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if( modelClass.isAssignableFrom(QuestionViewModel::class.java)){
            QuestionViewModel(this.questionRepository) as T
        }else{
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}