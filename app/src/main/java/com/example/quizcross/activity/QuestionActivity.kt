package com.example.quizcross.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.R
import com.example.quizcross.databinding.ActivityQuestionBinding
import com.example.quizcross.model.Settings
import com.example.quizcross.repository.QuestionRepository
import com.example.quizcross.repository.SettingRepository
import com.example.quizcross.viewmodel.QuestionViewModel
import com.example.quizcross.viewmodel.SettingsViewModel
import com.example.quizcross.viewmodel.factory.QuestionViewModelFactory
import com.example.quizcross.viewmodel.factory.SettingsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class QuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var questionViewModel: QuestionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        instantiateViewModel()
        setObserve()
        settingsViewModel.getSettings()

        binding.submitButton.setOnClickListener {
            val radioButtonId = binding.alternatives.checkedRadioButtonId
            if(radioButtonId != 1){
                val radioButton = findViewById<RadioButton>(radioButtonId)
                questionViewModel.checkAnswer(radioButton.text.toString())
            }else{
                Snackbar.make(
                    binding.root,
                    getString(R.string.warning_unselected_alternative),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun instantiateViewModel(){
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val repository = SettingRepository(sharedPreferences)
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(repository)
        ).get(SettingsViewModel::class.java)

        val questionRepository = QuestionRepository()
        questionViewModel = ViewModelProvider(
            this,
            QuestionViewModelFactory(questionRepository)
        ).get(QuestionViewModel::class.java)
    }

    private fun setObserve(){
        questionViewModel.question.observe(this, Observer {
            binding.questionStatementTextView.text = it.quetion
            val alternatives = mutableListOf<String>()
            alternatives.add(it.correct_answer)
            alternatives.add(it.incorrect_answers[0])
            alternatives.add(it.incorrect_answers[1])
            alternatives.add(it.incorrect_answers[2])
            alternatives.shuffle()
            binding.alternativeARadioButton.text = alternatives[0]
            binding.alternativeBRadioButton.text = alternatives[1]
            binding.alternativeCRadioButton.text = alternatives[2]
            binding.alternativeDRadioButton.text = alternatives[3]
        })

        questionViewModel.isCorrectAnswer.observe(this, Observer {
            if(it){
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.right_answer_title))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(getString(R.string.right_answer_message))
                    .setCancelable(true)
                    .setOnCancelListener { finish() }
                    .setPositiveButton(getString(R.string.close_button)
                    ) { _, _ -> finish() }
                    .show()
            }else{
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.wrong_answer_Title))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(getString(R.string.wrong_answer_message))
                    .setCancelable(true)
                    .setOnCancelListener { finish() }
                    .setPositiveButton(getString(R.string.close_button)
                    ) { _, _ -> finish() }
                    .show()
            }
        })

        settingsViewModel.setting.observe(this, Observer {
            val listCategory = mutableListOf<Int>()
            if(it.art){
                listCategory.add(Settings.ART_CODE)
            }
            if(it.animals){
                listCategory.add(Settings.ANIMALS_CODE)
            }
            if(it.films){
                listCategory.add(Settings.FILMS_CODE)
            }
            if(it.scienceNature){
                listCategory.add(Settings.SCIENCE_NATURE_CODE)
            }
            if(it.history){
                listCategory.add(Settings.HISTORY_CODE)
            }
            if(it.geography){
                listCategory.add(Settings.GEOGRAPHY_CODE)
            }
            if(it.mathematics){
                listCategory.add(Settings.MATHEMATICS_CODE)
            }
            if(it.generalKnowledge){
                listCategory.add(Settings.GENERAL_KNOWLEDGE_CODE)
            }

            val category = listCategory[Random.nextInt(listCategory.size)]
            questionViewModel.getQuestion(category, it.difficulty)
        })
    }


}