package com.example.quizcross.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizcross.R
import com.example.quizcross.model.ResultModel
import com.example.quizcross.model.Settings
import com.example.quizcross.repository.SettingRepository
import com.example.quizcross.service.AppConstants

class SettingsViewModel(private val application: Application, private val repository: SettingRepository)
    : AndroidViewModel(application) {

    private val _loadSettingsProcess = MutableLiveData<ResultModel<Settings>>()
    val loadSettingsProcess: LiveData<ResultModel<Settings>> = _loadSettingsProcess
    private val _saveSettingsProcess = MutableLiveData<ResultModel<Unit>>()
    val saveSettingsProcess: LiveData<ResultModel<Unit>> = _saveSettingsProcess

    private val _settings = MutableLiveData<Settings>()
    val setting: LiveData<Settings> = _settings
    private val _warningMessage = MutableLiveData<Int>()
    val warningMessage: LiveData<Int> = _warningMessage
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    fun getSettings(){
        _loadSettingsProcess.value = ResultModel.Loading
        val result = repository.getSetting()
        if(result!=null){
            _loadSettingsProcess.value = ResultModel.Success(result)
            _settings.postValue(result!!)
        }else{
            val settings = Settings(
                player1Name = AppConstants.SETTINGS.PLAYER_1_NAME,
                player2Name = AppConstants.SETTINGS.PLAYER_2_NAME,
                difficulty = AppConstants.SETTINGS.MEDIUM_DIFFICULTY,
                scienceNature = true,
                mathematics = true,
                geography = true,
                history = true,
                art = true,
                animals = true,
                films = true,
                generalKnowledge = true,
            )
            _settings.postValue(settings)
            _loadSettingsProcess.value = ResultModel.Success(settings)
        }
    }

    fun saveSettings(settings: Settings){
        _saveSettingsProcess.value = ResultModel.Loading
        try {
            val categoryValidation = (settings.generalKnowledge || settings.art || settings.animals
                    || settings.history || settings.history || settings.mathematics || settings.films
                    || settings.scienceNature)
            val playerNameValidation = (settings.player1Name != settings.player2Name)
            if(categoryValidation && playerNameValidation){
                repository.saveSetting(settings)
                _saveSettingsProcess.value = ResultModel.Success(Unit)
                _settings.postValue(settings)
                _errorMessage.postValue("")
            }else{
                if(!categoryValidation){
                    _saveSettingsProcess.value = ResultModel.Error(application.getString(R.string.error_message_category))
                }
                if(!playerNameValidation){
                    _saveSettingsProcess.value = ResultModel.Error(application.getString(R.string.error_message_player_name))
                }
                _warningMessage.postValue(R.string.error_message_category)
            }
        }catch (e: Exception){
            _errorMessage.postValue(e.message)
        }

    }


}