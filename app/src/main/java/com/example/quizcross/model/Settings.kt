package com.example.quizcross.model

data class Settings(
    var player1Name: String,
    var player2Name: String,
    var difficulty: String,
    var scienceNature: Boolean,
    var mathematics: Boolean,
    var geography: Boolean,
    var history: Boolean,
    var art: Boolean,
    var animals: Boolean,
    var films: Boolean,
    var generalKnowledge:Boolean
)