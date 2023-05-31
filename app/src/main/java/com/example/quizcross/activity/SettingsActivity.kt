package com.example.quizcross.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.R
import com.example.quizcross.databinding.ActivitySettingsBinding
import com.example.quizcross.databinding.EditTextAlertDialogBinding
import com.example.quizcross.model.ResultModel
import com.example.quizcross.model.Settings
import com.example.quizcross.repository.SettingRepository
import com.example.quizcross.service.AppConstants
import com.example.quizcross.viewmodel.SettingsViewModel
import com.example.quizcross.viewmodel.factory.SettingsViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        instantiateViewModel()
        setObserves()
        viewModel.getSettings()

        binding.saveSettingsButton.setOnClickListener {
            viewModel.saveSettings(getSettingsData())
        }
        binding.editPlayer1NameButton.setOnClickListener {
            showEditNameAlertDialog(getSettingsData(), binding.textPlayer1Name)
        }
        binding.editPlayer2NameButton.setOnClickListener {
            showEditNameAlertDialog(getSettingsData(), binding.textPlayer2Name)
        }

    }

    private fun instantiateViewModel(){
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val repository = SettingRepository(sharedPreferences)
        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(application, repository)
        )[SettingsViewModel::class.java]
    }

    private fun setObserves(){
        viewModel.loadSettingsProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    binding.saveSettingsButton.isClickable = true
                    binding.textPlayer1Name.text = it.data.player1Name
                    binding.textPlayer2Name.text = it.data.player2Name
                    when(it.data.difficulty){
                        AppConstants.SETTINGS.EASY_DIFFICULTY -> binding.difficultChipGroup.check(binding.easyChip.id)
                        AppConstants.SETTINGS.MEDIUM_DIFFICULTY -> binding.difficultChipGroup.check(binding.mediumChip.id)
                        AppConstants.SETTINGS.HARD_DIFFICULTY -> binding.difficultChipGroup.check(binding.hardChip.id)
                        else -> binding.difficultChipGroup.check(binding.mediumChip.id)
                    }
                    binding.filmsCheckBox.isChecked = it.data.films
                    binding.generalKnowledgeCheckBox.isChecked = it.data.generalKnowledge
                    binding.geographyCheckBox.isChecked = it.data.geography
                    binding.historyCheckBox.isChecked = it.data.history
                    binding.scienceNatureCheckBox.isChecked = it.data.scienceNature
                    binding.artCheckBox.isChecked = it.data.art
                    binding.animalsCheckBox.isChecked = it.data.animals
                    binding.mathematicsCheckBox.isChecked = it.data.mathematics
                }
                is ResultModel.Error -> {
                    binding.saveSettingsButton.isClickable = true
                    showSnackbar(it.message.toString())
                    finish()
                }
                is ResultModel.Loading -> {
                    binding.saveSettingsButton.isClickable = false
                }
            }
        }

        viewModel.saveSettingsProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    binding.progressBarSaveSettings.visibility = View.GONE
                    binding.saveSettingsButton.isClickable = true
                    binding.saveSettingsButton.text = getString(R.string.save_button)
                    viewModel.getSettings()
                    showSnackbar(getString(R.string.success_message))
                }
                is ResultModel.Error -> {
                    binding.progressBarSaveSettings.visibility = View.GONE
                    binding.saveSettingsButton.isClickable = true
                    binding.saveSettingsButton.text = getString(R.string.save_button)
                    showWarningAlertDialog(it.message.toString())
                }
                is ResultModel.Loading -> {
                    binding.progressBarSaveSettings.visibility = View.VISIBLE
                    binding.saveSettingsButton.isClickable = false
                    binding.saveSettingsButton.text = ""
                }
            }
        }
    }

    private fun showSnackbar(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
            .setTextColor(ContextCompat.getColor(this, R.color.button_color))
            .show()
    }

    private fun showWarningAlertDialog(message: String){
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.warning_title))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(message)
            .setPositiveButton(getString(R.string.close_button)
            ) { _, _ -> }
            .show()
    }

    private fun showEditNameAlertDialog(settingModel: Settings, textView: TextView) {
        val view = EditTextAlertDialogBinding.inflate(layoutInflater)
        view.commentEditText.setText(textView.text.toString())

        val alertDialogBuilder = AlertDialog.Builder(this)
        if(textView.id == binding.textPlayer1Name.id){
            alertDialogBuilder.setTitle(getString(R.string.player1))
        }else{
            alertDialogBuilder.setTitle(getString(R.string.player2))
        }
        alertDialogBuilder.setMessage(getString(R.string.edit_name_player))
        alertDialogBuilder.setView(view.root)
        alertDialogBuilder.setPositiveButton(getString(R.string.save_button)) { _, _ ->
            if(textView.id == binding.textPlayer1Name.id){
                settingModel.player1Name = view.commentEditText.text.toString()
            }else{
                settingModel.player2Name = view.commentEditText.text.toString()
            }
            viewModel.saveSettings(settingModel)
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()
    }

    private fun getSettingsData(): Settings {
        val difficult = findViewById<Chip>(binding.difficultChipGroup.checkedChipId).text.toString().lowercase()
        return Settings(
            player1Name = binding.textPlayer1Name.text.toString(),
            player2Name = binding.textPlayer2Name.text.toString(),
            difficult,
            scienceNature = binding.scienceNatureCheckBox.isChecked,
            mathematics = binding.mathematicsCheckBox.isChecked,
            geography = binding.geographyCheckBox.isChecked,
            history = binding.historyCheckBox.isChecked,
            art = binding.artCheckBox.isChecked,
            films = binding.filmsCheckBox.isChecked,
            animals = binding.animalsCheckBox.isChecked,
            generalKnowledge = binding.generalKnowledgeCheckBox.isChecked
        )
    }

}