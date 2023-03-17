package com.example.quizcross

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.databinding.FragmentGamePlayBinding
import com.example.quizcross.viewmodel.GameViewModel
import com.example.quizcross.viewmodel.QuestionViewModel
import com.example.quizcross.viewmodel.factory.QuestionViewModelFactory
import com.google.android.material.snackbar.Snackbar

class GamePlayFragment : Fragment() {

    private lateinit var binding: FragmentGamePlayBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var questionViewModel: QuestionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentGamePlayBinding.inflate(layoutInflater)

        gameViewModel = activity?.run {
            ViewModelProvider(this).get(GameViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        questionViewModel = (activity as GamePlayActivity).getQuestionViewModel()


        questionViewModel.isCorrectAnswer.observe(this, Observer {
            println("IS_CURRECT_ANSWER: $it")
        })




        gameViewModel.isPlayer1Winner.observe(this, Observer {
            if (it){
                gameViewModel.newGame()
                resetMatrix()
                Snackbar.make(
                    binding.root,
                    "Vitoria do Player1",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

        gameViewModel.isPlayer2Winner.observe(this, Observer {
            if(it){
                gameViewModel.newGame()
                resetMatrix()
            }
        })

        gameViewModel.ticTacToe.observe(this, Observer {
            if(it.isPlayer1Turn){
                binding.turnText.text = "Turn: Player 1"
            }else{
                binding.turnText.text = "Turn: Player 2"
            }
            if(it.isTie){
                gameViewModel.newGame()
                resetMatrix()

            }
        })

        gameViewModel.scoreboard.observe(this, Observer {
            binding.textWinsPlayer1.text = it.player1.toString()
            binding.textWinsPlayer2.text = it.player2.toString()
            if(it.player1 == 3 && it.player2 !=3){
                showWinDialog(requireContext(), "Player 1")
                disableButtonsMatrix()
            }else if (it.player2 == 3 && it.player1 !=3){
                showWinDialog(requireContext(), "Player 2")
                disableButtonsMatrix()
            }else if (it.player2 == 3 && it.player1 == it.player2){
                showWinDialog(requireContext(), null)
                disableButtonsMatrix()
            }
        })


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentGamePlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingMatrix()
    }

    private fun showQuestion(){
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, QuestionFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun settingMatrix(){
        binding.matrix.position00.setOnClickListener {
//            showQuestion()
            gameViewModel.makeMove(0,0)
            setImageButtonMatrix(binding.matrix.position00)
        }
        binding.matrix.position01.setOnClickListener {
            showQuestion()
            gameViewModel.makeMove(0,1)
            setImageButtonMatrix(binding.matrix.position01)
        }
        binding.matrix.position02.setOnClickListener {
            showQuestion()
            gameViewModel.makeMove(0,2)
            setImageButtonMatrix(binding.matrix.position02)
        }
        binding.matrix.position10.setOnClickListener {
            showQuestion()
            gameViewModel.makeMove(1,0)
            setImageButtonMatrix(binding.matrix.position10)
        }
        binding.matrix.position11.setOnClickListener {
            showQuestion()
            gameViewModel.makeMove(1,1)
            setImageButtonMatrix(binding.matrix.position11)
        }
        binding.matrix.position12.setOnClickListener {
            showQuestion()
            gameViewModel.makeMove(1,2)
            setImageButtonMatrix(binding.matrix.position12)
        }
        binding.matrix.position20.setOnClickListener {
            showQuestion()
            gameViewModel.makeMove(2,0)
            setImageButtonMatrix(binding.matrix.position20)
        }
        binding.matrix.position21.setOnClickListener {
            showQuestion()
            gameViewModel.makeMove(2,1)
            setImageButtonMatrix(binding.matrix.position21)
        }
        binding.matrix.position22.setOnClickListener {
            showQuestion()
            gameViewModel.makeMove(2,2)
            setImageButtonMatrix(binding.matrix.position22)
        }
    }

    private fun setImageButtonMatrix(imgButton: ImageButton){
        if(gameViewModel.ticTacToe.value!!.isPlayer1Turn){
            imgButton.setImageResource(R.drawable.ic_cross)
        }else{
            imgButton.setImageResource(R.drawable.ic_circle)
        }
        imgButton.isEnabled = false
    }

    private fun resetMatrix(){
        binding.matrix.position00.isEnabled = true
        binding.matrix.position01.isEnabled = true
        binding.matrix.position02.isEnabled = true
        binding.matrix.position10.isEnabled = true
        binding.matrix.position11.isEnabled = true
        binding.matrix.position12.isEnabled = true
        binding.matrix.position20.isEnabled = true
        binding.matrix.position21.isEnabled = true
        binding.matrix.position22.isEnabled = true

        binding.matrix.position00.setImageResource(android.R.color.transparent)
        binding.matrix.position01.setImageResource(android.R.color.transparent)
        binding.matrix.position02.setImageResource(android.R.color.transparent)
        binding.matrix.position10.setImageResource(android.R.color.transparent)
        binding.matrix.position11.setImageResource(android.R.color.transparent)
        binding.matrix.position12.setImageResource(android.R.color.transparent)
        binding.matrix.position20.setImageResource(android.R.color.transparent)
        binding.matrix.position21.setImageResource(android.R.color.transparent)
        binding.matrix.position22.setImageResource(android.R.color.transparent)
    }
    private fun disableButtonsMatrix(){
        binding.matrix.position00.isEnabled = false
        binding.matrix.position01.isEnabled = false
        binding.matrix.position02.isEnabled = false
        binding.matrix.position10.isEnabled = false
        binding.matrix.position11.isEnabled = false
        binding.matrix.position12.isEnabled = false
        binding.matrix.position20.isEnabled = false
        binding.matrix.position21.isEnabled = false
        binding.matrix.position22.isEnabled = false
    }

    private fun showWinDialog(context: Context, winnerName: String?) {
        if(winnerName!=null){
            AlertDialog.Builder(requireContext())
                .setTitle("Parabéns $winnerName!")
                .setMessage("Você venceu a partida!")
                .setPositiveButton("Jogar Novamente", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        gameViewModel.newGame()
                        activity?.recreate()
                    }
                })
                .setNegativeButton("Ir Para o Menu", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        activity?.finish()
                        dialog?.dismiss()
                    }
                })
                .show()
        }else{
            AlertDialog.Builder(requireContext())
                .setTitle("Empate!")
                .setMessage("Nunhum jogador venceu!")
                .setPositiveButton("Jogar Novamente", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        gameViewModel.newGame()
                        activity?.recreate()
                    }
                })
                .setNegativeButton("Ir Para o Menu", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        activity?.finish()
                        dialog?.dismiss()
                    }
                })
                .show()
        }
    }
}