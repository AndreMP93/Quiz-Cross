package com.example.quizcross.activity

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.quizcross.R
import com.example.quizcross.databinding.ActivityNewGameBinding
import com.example.quizcross.viewmodel.GameViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates

class NewGameActivity : AppCompatActivity() {

    private lateinit var newGameBinding: ActivityNewGameBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newGameBinding = ActivityNewGameBinding.inflate(layoutInflater)
        setContentView(newGameBinding.root)


        viewModel = GameViewModel()

        settingMatrix()

        viewModel.isPlayer1Winner.observe(this, Observer {
            if (it){
                viewModel.newGame()
                resetMatrix()
                Snackbar.make(
                    newGameBinding.root,
                    "Vitoria do Player1",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

        viewModel.isPlayer2Winner.observe(this, Observer {
            if(it){
                viewModel.newGame()
                resetMatrix()
            }
        })

        viewModel.ticTacToe.observe(this, Observer {
            if(it.isPlayer1Turn){
                newGameBinding.textTurn.text = "Turn: Player 1"
            }else{
                newGameBinding.textTurn.text = "Turn: Player 2"
            }
            if(it.isTie){
                viewModel.newGame()
                resetMatrix()

            }
        })

        viewModel.scoreboard.observe(this, Observer {
            newGameBinding.textWinsPlayer1.text = it.player1.toString()
            newGameBinding.textWinsPlayer2.text = it.player2.toString()
            if(it.player1 == 3 && it.player2 !=3){
                showWinDialog(applicationContext, "Player 1")
                disableButtonsMatrix()
            }else if (it.player2 == 3 && it.player1 !=3){
                showWinDialog(applicationContext, "Player 2")
                disableButtonsMatrix()
            }else if (it.player2 == 3 && it.player1 == it.player2){
                showWinDialog(applicationContext, null)
                disableButtonsMatrix()
            }
        })

    }

    private fun settingMatrix(){
        newGameBinding.matrix.position00.setOnClickListener {
            viewModel.makeMove(0,0)
            setImageButtonMatrix(newGameBinding.matrix.position00)
        }
        newGameBinding.matrix.position01.setOnClickListener {
            viewModel.makeMove(0,1)
            setImageButtonMatrix(newGameBinding.matrix.position01)
        }
        newGameBinding.matrix.position02.setOnClickListener {
            viewModel.makeMove(0,2)
            setImageButtonMatrix(newGameBinding.matrix.position02)
        }
        newGameBinding.matrix.position10.setOnClickListener {
            viewModel.makeMove(1,0)
            setImageButtonMatrix(newGameBinding.matrix.position10)
        }
        newGameBinding.matrix.position11.setOnClickListener {
            viewModel.makeMove(1,1)
            setImageButtonMatrix(newGameBinding.matrix.position11)
        }
        newGameBinding.matrix.position12.setOnClickListener {
            viewModel.makeMove(1,2)
            setImageButtonMatrix(newGameBinding.matrix.position12)
        }
        newGameBinding.matrix.position20.setOnClickListener {
            viewModel.makeMove(2,0)
            setImageButtonMatrix(newGameBinding.matrix.position20)
        }
        newGameBinding.matrix.position21.setOnClickListener {
            viewModel.makeMove(2,1)
            setImageButtonMatrix(newGameBinding.matrix.position21)
        }
        newGameBinding.matrix.position22.setOnClickListener {
            viewModel.makeMove(2,2)
            setImageButtonMatrix(newGameBinding.matrix.position22)
        }
    }

    private fun setImageButtonMatrix(imgButton: ImageButton){
        if(viewModel.ticTacToe.value!!.isPlayer1Turn){
            imgButton.setImageResource(R.drawable.ic_cross)
        }else{
            imgButton.setImageResource(R.drawable.ic_circle)
        }
        imgButton.isEnabled = false
    }

    private fun resetMatrix(){
        newGameBinding.matrix.position00.isEnabled = true
        newGameBinding.matrix.position01.isEnabled = true
        newGameBinding.matrix.position02.isEnabled = true
        newGameBinding.matrix.position10.isEnabled = true
        newGameBinding.matrix.position11.isEnabled = true
        newGameBinding.matrix.position12.isEnabled = true
        newGameBinding.matrix.position20.isEnabled = true
        newGameBinding.matrix.position21.isEnabled = true
        newGameBinding.matrix.position22.isEnabled = true

        newGameBinding.matrix.position00.setImageResource(android.R.color.transparent)
        newGameBinding.matrix.position01.setImageResource(android.R.color.transparent)
        newGameBinding.matrix.position02.setImageResource(android.R.color.transparent)
        newGameBinding.matrix.position10.setImageResource(android.R.color.transparent)
        newGameBinding.matrix.position11.setImageResource(android.R.color.transparent)
        newGameBinding.matrix.position12.setImageResource(android.R.color.transparent)
        newGameBinding.matrix.position20.setImageResource(android.R.color.transparent)
        newGameBinding.matrix.position21.setImageResource(android.R.color.transparent)
        newGameBinding.matrix.position22.setImageResource(android.R.color.transparent)
    }
    private fun disableButtonsMatrix(){
        newGameBinding.matrix.position00.isEnabled = false
        newGameBinding.matrix.position01.isEnabled = false
        newGameBinding.matrix.position02.isEnabled = false
        newGameBinding.matrix.position10.isEnabled = false
        newGameBinding.matrix.position11.isEnabled = false
        newGameBinding.matrix.position12.isEnabled = false
        newGameBinding.matrix.position20.isEnabled = false
        newGameBinding.matrix.position21.isEnabled = false
        newGameBinding.matrix.position22.isEnabled = false
    }

    private fun showWinDialog(context: Context, winnerName: String?) {
        if(winnerName!=null){
            AlertDialog.Builder(this)
                .setTitle("Parabéns $winnerName!")
                .setMessage("Você venceu a partida!")
                .setPositiveButton("Jogar Novamente", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        viewModel.newGame()
                        recreate()
                    }
                })
                .setNegativeButton("Ir Para o Menu", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        finish()
                        dialog?.dismiss()
                    }
                })
                .show()
        }else{
            AlertDialog.Builder(this)
                .setTitle("Empate!")
                .setMessage("Nunhum jogador venceu!")
                .setPositiveButton("Jogar Novamente", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        viewModel.newGame()
                        recreate()
                    }
                })
                .setNegativeButton("Ir Para o Menu", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        finish()
                        dialog?.dismiss()
                    }
                })
                .show()
        }
    }

}