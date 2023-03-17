package com.example.quizcross.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizcross.R
import com.example.quizcross.model.Settings
import com.example.quizcross.repository.SettingRepository

class SettingsViewModel(private val repository: SettingRepository): ViewModel() {

    private val _settings = MutableLiveData<Settings>()
    val setting: LiveData<Settings> = _settings
    private val _warningMessage = MutableLiveData<Int>()
    val warningMessage: LiveData<Int> = _warningMessage
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    fun getSettings(){
        val result = repository.getSetting()
        if(result!=null){
        _settings.postValue(result!!)
        }else{
            _settings.postValue(Settings(
                difficulty = Settings.MEDIUM_DIFFICULTY,
                scienceNature = true,
                mathematics = true,
                geography = true,
                history = true,
                art = true,
                animals = true,
                films = true,
                generalKnowledge = true,
            ))
        }
    }

    fun saveSettings(settings: Settings){
        try {
            if(
                settings.generalKnowledge || settings.art || settings.animals || settings.history
                || settings.history || settings.mathematics || settings.films || settings.scienceNature
            ){
                repository.saveSetting(settings)
                _settings.postValue(settings)
                _errorMessage.postValue("")
            }else{
                _warningMessage.postValue(R.string.error_message_category)
            }
        }catch (e: Exception){
            _errorMessage.postValue(e.message)
        }

    }


}