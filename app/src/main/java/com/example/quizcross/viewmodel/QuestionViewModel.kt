package com.example.quizcross.viewmodel

import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizcross.model.Question
import com.example.quizcross.model.ResultModel
import com.example.quizcross.repository.QuestionRepository
import com.example.quizcross.service.APIListener
import kotlinx.coroutines.launch

class QuestionViewModel(private val questionRepository: QuestionRepository): ViewModel() {
    private val _loadQuestionProcess = MutableLiveData<ResultModel<Question>>()
    val loadQuestionProcess: LiveData<ResultModel<Question>> = _loadQuestionProcess

    private val _isCorrectAnswer = MutableLiveData<Boolean>()
    val isCorrectAnswer: LiveData<Boolean> = _isCorrectAnswer


    fun getQuestion(category: Int, difficult: String){
        viewModelScope.launch {
            try {
                _loadQuestionProcess.value = ResultModel.Loading
                questionRepository.loadQuestion(category, difficult, object : APIListener<Question>{
                    override fun onSuccess(result: ResultModel.Success<Question>) {
                        val question = result.data
                        var decodedContent = HtmlCompat.fromHtml(question.question, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        question.question = decodedContent
                        decodedContent = HtmlCompat.fromHtml(question.correct_answer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        question.correct_answer = decodedContent

                        val decodedIncorrectAnswers = question.incorrect_answers.map { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY).toString() }
                        question.incorrect_answers = decodedIncorrectAnswers
                        _loadQuestionProcess.value = result
                    }

                    override fun onFailure(onMessage: String) {
                        _loadQuestionProcess.value = ResultModel.Error(onMessage)
                    }
                })


            }catch (e: Exception){
                println("ERROR (getQuestion): ${e.message}")
                _loadQuestionProcess.value = ResultModel.Error(e.message)
            }
        }
    }

    fun checkAnswer(answer: String){
        if(_loadQuestionProcess.value is ResultModel.Success){
            val question = (_loadQuestionProcess.value as ResultModel.Success<Question>).data
            if(question.correct_answer == answer){
                _isCorrectAnswer.postValue(true)
            }else{
                _isCorrectAnswer.postValue(false)
            }
        }

    }
}