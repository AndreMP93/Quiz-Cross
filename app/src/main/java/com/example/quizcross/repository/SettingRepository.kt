package com.example.quizcross.repository

import android.content.SharedPreferences
import com.example.quizcross.model.Settings
import com.google.gson.Gson

class SettingRepository(private val sharedPreferences: SharedPreferences){
    private val settingsKey = "userSettings"
    private val gson = Gson()

    fun saveSetting(settings: Settings){
        val json = gson.toJson(settings)
        sharedPreferences.edit().putString(settingsKey, json).apply()
    }

    fun getSetting(): Settings? {
        val json = sharedPreferences.getString(settingsKey, null)
        return gson.fromJson(json, Settings::class.java)
    }
}