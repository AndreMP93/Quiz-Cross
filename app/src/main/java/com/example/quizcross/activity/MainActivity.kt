package com.example.quizcross.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizcross.GamePlayActivity
import com.example.quizcross.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.buttonNewGame.setOnClickListener {
//            startActivity(Intent(applicationContext, NewGameActivity::class.java))
            startActivity(Intent(applicationContext, GamePlayActivity::class.java))
        }
        mainBinding.buttonSettings.setOnClickListener {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
        }
    }
}