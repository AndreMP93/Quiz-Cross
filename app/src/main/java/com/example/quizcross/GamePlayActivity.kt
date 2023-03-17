package com.example.quizcross

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.databinding.ActivityGamePlayBinding
import com.example.quizcross.repository.QuestionRepository
import com.example.quizcross.repository.SettingRepository
import com.example.quizcross.viewmodel.GameViewModel
import com.example.quizcross.viewmodel.QuestionViewModel
import com.example.quizcross.viewmodel.SettingsViewModel
import com.example.quizcross.viewmodel.factory.QuestionViewModelFactory
import com.example.quizcross.viewmodel.factory.SettingsViewModelFactory

class GamePlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGamePlayBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var questionViewModel: QuestionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

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
        binding = ActivityGamePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        supportFragmentManager.beginTransaction()
//            .add(R.id.fragmentContainerView, GamePlayFragment())
//            .commit()
    }

    fun getQuestionViewModel(): QuestionViewModel{
        return questionViewModel
    }

    fun getSettingsViewModel(): SettingsViewModel{
        return settingsViewModel
    }


}