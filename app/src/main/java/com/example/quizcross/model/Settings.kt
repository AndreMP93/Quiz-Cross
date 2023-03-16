package com.example.quizcross.model

data class Settings(
    var difficulty: String,
    var scienceNature: Boolean,
    var mathematics: Boolean,
    var geography: Boolean,
    var history: Boolean,
    var art: Boolean,
    var animals: Boolean,
    var films: Boolean,
    var generalKnowledge:Boolean
) {

    companion object {
        // https://opentdb.com/api.php?amount=1&category=9&difficulty=medium&type=multiple
        const val EASY_DIFFICULTY = "easy"
        const val MEDIUM_DIFFICULTY = "medium"
        const val HARD_DIFFICULTY = "hard"

        const val GENERAL_KNOWLEDGE_CODE = 9
        const val FILMS_CODE = 11
        const val SCIENCE_NATURE_CODE = 17
        const val MATHEMATICS_CODE = 19
        const val GEOGRAPHY_CODE = 22
        const val HISTORY_CODE = 23
        const val ART_CODE = 25
        const val ANIMALS_CODE = 27

    }
}