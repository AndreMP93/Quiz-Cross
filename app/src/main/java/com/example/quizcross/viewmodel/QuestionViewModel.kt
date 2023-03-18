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
    private val _isRequestingQuestion = MutableLiveData<Boolean>()
    val isRequestingQuestion: LiveData<Boolean> = _isRequestingQuestion

    init {
        _isRequestingQuestion.postValue(false)
    }

    fun getQuestion(category: Int, difficult: String){
        viewModelScope.launch {
            try {
                _isRequestingQuestion.postValue(true)
                val response = questionRepository.getQuestion(category, difficult)
                val question = response.results.first()
                _question.postValue(question)
                _isRequestingQuestion.postValue(false)

            }catch (e: Exception){
                println("ERROR: ${e.message}")
                _isRequestingQuestion.postValue(false)
            }
        }
    }

    fun checkAnswer(answer: String){
        if(_question.value != null && _question.value!!.correct_answer == answer){
            _isCorrectAnswer.postValue(true)
        }else{
            _isCorrectAnswer.postValue(false)
        }
    }
}