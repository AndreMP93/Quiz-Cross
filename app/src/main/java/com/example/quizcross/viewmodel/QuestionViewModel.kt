package com.example.quizcross.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizcross.model.Question
import com.example.quizcross.repository.QuestionRepository
import kotlinx.coroutines.launch

class QuestionViewModel(private val questionRepository: QuestionRepository): ViewModel() {
    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> = _question
    private val _isCorrectAnswer = MutableLiveData<Boolean>()
    val isCorrectAnswer: LiveData<Boolean> = _isCorrectAnswer

    fun getQuestion(category: Int, difficult: String){
        viewModelScope.launch {
            try {
                val response = questionRepository.getQuestion(category, difficult)
                val question = response.results.first()
                _question.postValue(question)


            }catch (e: Exception){
                println("ERROR: ${e.message}")
            }
        }
    }

    fun checkAnswer(answer: String){
        println("IS_CURRECT_ANSWER: $answer")
        if(_question.value != null && _question.value!!.correct_answer == answer){
            _isCorrectAnswer.postValue(true)
        }else{
            _isCorrectAnswer.postValue(false)
        }
    }
}