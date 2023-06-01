package com.example.quizcross.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizcross.model.Scoreboard
import com.example.quizcross.model.TicTacToe
import kotlinx.coroutines.launch

class GameViewModel: ViewModel() {

    private val _scoreboard = MutableLiveData<Scoreboard>()
    private val _ticTacToe = MutableLiveData<TicTacToe>()
    private val _isPlayer1Winner = MutableLiveData<Boolean>()
    private val _isPlayer2Winner = MutableLiveData<Boolean>()

    val scoreboard: LiveData<Scoreboard> = _scoreboard
    val ticTacToe: LiveData<TicTacToe> = _ticTacToe
    val isPlayer1Winner: LiveData<Boolean> = _isPlayer1Winner
    val isPlayer2Winner: LiveData<Boolean> = _isPlayer2Winner

    init {
        _scoreboard.postValue(Scoreboard(player1 = 0, player2 = 0))
        _isPlayer1Winner.postValue(false)
        _isPlayer2Winner.postValue(false)
        _ticTacToe.postValue(TicTacToe(
            board = arrayOf(
                arrayOf("_","_","_"),
                arrayOf("_","_","_"),
                arrayOf("_","_","_")
            ),
            isPlayer1Turn = true,
            isEndGame = false,
            isTie = false
        ))
    }

    fun newGame(){
        _ticTacToe.postValue(TicTacToe(
            board = arrayOf(
                arrayOf("_","_","_"),
                arrayOf("_","_","_"),
                arrayOf("_","_","_")
            ),
            isPlayer1Turn = _isPlayer1Winner.value != true,
            isEndGame = false,
            isTie = false
        ))
        _isPlayer1Winner.postValue(false)
        _isPlayer2Winner.postValue(false)
    }

    fun makeMove(line: Int, column: Int, isCorrectAnswer: Boolean){
        viewModelScope.launch {
            val newTicTacToe = _ticTacToe.value?.let {
                TicTacToe(
                    it.board.copyOf(),
                    it.isPlayer1Turn,
                    it.isEndGame,
                    it.isTie
                )
            }
            if (newTicTacToe != null) {
                if (newTicTacToe.isPlayer1Turn){
                    if(isCorrectAnswer){
                        newTicTacToe.board[line][column] = TicTacToe.PLAYER_1_SYMBOL
                    }else{
                        newTicTacToe.board[line][column] = TicTacToe.PLAYER_2_SYMBOL
                    }
                }else{
                    if(isCorrectAnswer){
                        newTicTacToe.board[line][column] = TicTacToe.PLAYER_2_SYMBOL
                    }else{
                        newTicTacToe.board[line][column] = TicTacToe.PLAYER_1_SYMBOL
                    }
                }
                newTicTacToe.isPlayer1Turn = !(newTicTacToe.isPlayer1Turn)
                newTicTacToe.isEndGame = checkEndGame(newTicTacToe.board)
                newTicTacToe.isTie = (checkFullBoard(newTicTacToe.board)
                        && _isPlayer1Winner.value == _isPlayer2Winner.value)
                if (newTicTacToe.isTie){
                    updateScoreboard(1,1)
                }
                _ticTacToe.postValue(newTicTacToe!!)
            }

        }
    }

    private fun checkEndGame(board: Array<Array<String>>): Boolean{
        for(line in board){
            if(line[0] == TicTacToe.PLAYER_1_SYMBOL && line[0] == line[1] && line[0] == line[2]){
                _isPlayer1Winner.postValue(true)
                updateScoreboard(1,0)
                return true
            }
            if(line[0] == TicTacToe.PLAYER_2_SYMBOL && line[0] == line[1] && line[0] == line[2]){
                _isPlayer2Winner.postValue(true)
                updateScoreboard(0,1)
                return true
            }
        }
        for(column in board.indices){
            if(board[0][column] == TicTacToe.PLAYER_1_SYMBOL && board[0][column] == board[1][column] && board[0][column] == board[2][column]){
                _isPlayer1Winner.postValue(true)
                updateScoreboard(1,0)
                return true
            }
            if(board[0][column] == TicTacToe.PLAYER_2_SYMBOL && board[0][column] == board[1][column] && board[0][column] == board[2][column]){
                _isPlayer2Winner.postValue(true)
                updateScoreboard(0,1)
                return true
            }
        }
        if (board[1][1] == TicTacToe.PLAYER_1_SYMBOL && ((board[0][0] == board[1][1] && board[0][0] == board[2][2])
            || (board[0][2] == board[1][1] && board[1][1] == board[2][0]))){
            _isPlayer1Winner.postValue(true)
            updateScoreboard(1,0)
            return true
        }
        if (board[1][1] == TicTacToe.PLAYER_2_SYMBOL && ((board[0][0] == board[1][1] && board[0][0] == board[2][2])
            || (board[0][2] == board[1][1] && board[1][1] == board[2][0]))){
            _isPlayer2Winner.postValue(true)
            updateScoreboard(0,1)
            return true
        }

        return checkFullBoard(board)

    }

    private fun checkFullBoard(board: Array<Array<String>>):Boolean{
        for (line in board.indices){
            for (column in board.indices){
                if(board[line][column] == "_"){
                    return false
                }
            }
        }
        return true
    }

    private fun updateScoreboard(player1: Int, player2: Int){
        val player1Wins: Int = _scoreboard.value?.player1!!
        val player2Wins: Int = _scoreboard.value?.player2!!
        val newScoreboard = Scoreboard(player1 = player1Wins+player1,player2 = player2Wins+player2)
        _scoreboard.postValue(newScoreboard)
    }
}