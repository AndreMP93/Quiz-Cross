package com.example.quizcross.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizcross.databinding.ActivityQuestionBinding

class QuestionActivity : AppCompatActivity() {

    lateinit var binding: ActivityQuestionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}