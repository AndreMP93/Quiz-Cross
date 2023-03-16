package com.example.quizcross.model

data class TicTacToe(
    val board: Array<Array<String>>,
    var isPlayer1Turn: Boolean,
    var isEndGame: Boolean,
    var isTie: Boolean
) {
    companion object {
        const val PLAYER_1_SYMBOL = "X"
        const val PLAYER_2_SYMBOL = "O"
    }


}