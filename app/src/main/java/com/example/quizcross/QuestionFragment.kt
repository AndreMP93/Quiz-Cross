package com.example.quizcross

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.databinding.FragmentQuestionBinding
import com.example.quizcross.model.Settings
import com.example.quizcross.viewmodel.GameViewModel
import com.example.quizcross.viewmodel.QuestionViewModel
import com.example.quizcross.viewmodel.SettingsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class QuestionFragment : Fragment() {

    private lateinit var binding: FragmentQuestionBinding
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var questionViewModel: QuestionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentQuestionBinding.inflate(layoutInflater)

        settingsViewModel = (activity as GamePlayActivity).getSettingsViewModel()

        questionViewModel = (activity as GamePlayActivity).getQuestionViewModel()


        settingsViewModel.getSettings()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserve(viewLifecycleOwner)
        binding.submitButton.setOnClickListener {
            val radioButtonId = binding.alternatives.checkedRadioButtonId

            if(radioButtonId != 1){
                when(radioButtonId){
                    binding.alternativeARadioButton.id -> {
                        questionViewModel.checkAnswer(binding.alternativeARadioButton.text.toString())
                    }
                    binding.alternativeBRadioButton.id -> {
                        questionViewModel.checkAnswer(binding.alternativeBRadioButton.text.toString())
                    }
                    binding.alternativeCRadioButton.id -> {
                        questionViewModel.checkAnswer(binding.alternativeCRadioButton.text.toString())
                    }
                    binding.alternativeDRadioButton.id -> {
                        questionViewModel.checkAnswer(binding.alternativeDRadioButton.text.toString())
                    }
                    else -> {
                        println("ERROR: ________")

                    }

                }
                parentFragmentManager.popBackStack()

            }else{
                Snackbar.make(
                    binding.root,
                    getString(R.string.warning_unselected_alternative),
                    Snackbar.LENGTH_LONG
                ).show()
            }

        }

    }

    private fun setObserve(lifecycleOwner: LifecycleOwner){
        questionViewModel.question.observe(lifecycleOwner, Observer {
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

//        questionViewModel.isCorrectAnswer.observe(lifecycleOwner, Observer {
//            if(it){
//                AlertDialog.Builder(requireContext())
//                    .setTitle(getString(R.string.right_answer_title))
//                    .setIcon(android.R.drawable.ic_dialog_info)
//                    .setMessage(getString(R.string.right_answer_message))
//                    .setCancelable(true)
//                    .setOnCancelListener { parentFragmentManager.popBackStack() }
//                    .setPositiveButton(getString(R.string.close_button)
//                    ) { _, _ -> parentFragmentManager.popBackStack() }
//                    .show()
//            }else{
//                AlertDialog.Builder(requireContext())
//                    .setTitle(getString(R.string.wrong_answer_Title))
//                    .setIcon(android.R.drawable.ic_dialog_info)
//                    .setMessage(getString(R.string.wrong_answer_message))
//                    .setCancelable(true)
//                    .setOnCancelListener { parentFragmentManager.popBackStack() }
//                    .setPositiveButton(getString(R.string.close_button)
//                    ) { _, _ -> parentFragmentManager.popBackStack() }
//                    .show()
//            }
//        })

        settingsViewModel.setting.observe(lifecycleOwner, Observer {
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