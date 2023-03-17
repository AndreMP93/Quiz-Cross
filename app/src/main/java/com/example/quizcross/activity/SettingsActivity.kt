package com.example.quizcross.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.R
import com.example.quizcross.databinding.ActivitySettingsBinding
import com.example.quizcross.model.Settings
import com.example.quizcross.repository.SettingRepository
import com.example.quizcross.viewmodel.SettingsViewModel
import com.example.quizcross.viewmodel.factory.SettingsViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        instantiateViewModel()
        setObserves()
        viewModel.getSettings()
        binding.saveSettingsButton.setOnClickListener {
            val difficult = findViewById<Chip>(binding.difficultChipGroup.checkedChipId).text.toString().lowercase()
            val settings = Settings(
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
            viewModel.saveSettings(settings)
        }

//        binding.difficultChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
//            val chip:Chip? = findViewById<Chip>(checkedIds.first())
//            println("GROUP > ${chip?.text.toString().lowercase()}")
//        }
    }

    private fun instantiateViewModel(){
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val repository = SettingRepository(sharedPreferences)
        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(repository)
        ).get(SettingsViewModel::class.java)
    }

    private fun setObserves(){
        viewModel.setting.observe(this, Observer {
            when(it.difficulty){
                Settings.EASY_DIFFICULTY -> binding.difficultChipGroup.check(binding.easyChip.id)
                Settings.MEDIUM_DIFFICULTY -> binding.difficultChipGroup.check(binding.mediumChip.id)
                Settings.HARD_DIFFICULTY -> binding.difficultChipGroup.check(binding.hardChip.id)
                else -> binding.difficultChipGroup.check(binding.mediumChip.id)
            }
            binding.filmsCheckBox.isChecked = it.films
            binding.generalKnowledgeCheckBox.isChecked = it.generalKnowledge
            binding.geographyCheckBox.isChecked = it.geography
            binding.historyCheckBox.isChecked = it.history
            binding.scienceNatureCheckBox.isChecked = it.scienceNature
            binding.artCheckBox.isChecked = it.art
            binding.animalsCheckBox.isChecked = it.animals
            binding.mathematicsCheckBox.isChecked = it.mathematics
        })

        viewModel.warningMessage.observe(this, Observer {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.warning_title))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(it))
                .setPositiveButton(getString(R.string.close_button)
                ) { _, _ -> binding.generalKnowledgeCheckBox.isChecked = true }
                .show()
        })

        viewModel.errorMessage.observe(this, Observer {
            val message = if(it.equals("")) getString(R.string.success_message) else it
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
                .setTextColor(ContextCompat.getColor(this, R.color.button_color))
                .show()
        })
    }
}