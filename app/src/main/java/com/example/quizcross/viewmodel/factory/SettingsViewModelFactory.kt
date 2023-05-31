package com.example.quizcross.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.repository.SettingRepository
import com.example.quizcross.viewmodel.SettingsViewModel

class SettingsViewModelFactory(private val application: Application, private val repository: SettingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if( modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            SettingsViewModel(this.application ,this.repository) as T
        }else{
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}