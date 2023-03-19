package com.example.quizcross.activity

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.R
import com.example.quizcross.databinding.ActivityNewGameBinding
import com.example.quizcross.model.Question
import com.example.quizcross.model.Settings
import com.example.quizcross.model.TicTacToe
import com.example.quizcross.repository.QuestionRepository
import com.example.quizcross.repository.SettingRepository
import com.example.quizcross.viewmodel.GameViewModel
import com.example.quizcross.viewmodel.QuestionViewModel
import com.example.quizcross.viewmodel.SettingsViewModel
import com.example.quizcross.viewmodel.factory.QuestionViewModelFactory
import com.example.quizcross.viewmodel.factory.SettingsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates

class NewGameActivity : AppCompatActivity() {

    private lateinit var newGameBinding: ActivityNewGameBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var difficult: String
    private lateinit var categories: MutableList<Int>
    private var selectedLine by Delegates.notNull<Int>()
    private var selectedColumn by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newGameBinding = ActivityNewGameBinding.inflate(layoutInflater)
        setContentView(newGameBinding.root)


        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]

        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val repository = SettingRepository(sharedPreferences)
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(repository)
        )[SettingsViewModel::class.java]

        val questionRepository = QuestionRepository()
        questionViewModel = ViewModelProvider(
            this,
            QuestionViewModelFactory(questionRepository)
        )[QuestionViewModel::class.java]

        setSettingsObserves()
        setQuestionObserves()
        settingsViewModel.getSettings()
        settingMatrix()

        setGameObserves()

    }

    private fun setGameObserves(){
        gameViewModel.isPlayer1Winner.observe(this) {
            if (it) {
                gameViewModel.newGame()
                resetMatrix()
                showSnackbar(getString(R.string.player1) + " " + getString(R.string.win))
            }
        }

        gameViewModel.isPlayer2Winner.observe(this) {
            if (it) {
                gameViewModel.newGame()
                resetMatrix()
                showSnackbar(getString(R.string.player2) + " " + getString(R.string.win))
            }
        }

        gameViewModel.ticTacToe.observe(this){
            if(it.isPlayer1Turn){
                val turnOrder = getString(R.string.turn) + getString(R.string.player1)
                newGameBinding.textTurn.text = turnOrder
            }else{
                val turnOrder = getString(R.string.turn) + getString(R.string.player2)
                newGameBinding.textTurn.text = turnOrder
            }
            if(it.isTie){
                gameViewModel.newGame()
                resetMatrix()

            }
            updateMatrix(it.board)
        }

        gameViewModel.scoreboard.observe(this) {
            newGameBinding.textWinsPlayer1.text = it.player1.toString()
            newGameBinding.textWinsPlayer2.text = it.player2.toString()
            if (it.player1 == 3 && it.player2 != 3) {
                showWinDialog(getString(R.string.player1))
                disableButtonsMatrix()
            } else if (it.player2 == 3 && it.player1 != 3) {
                showWinDialog(getString(R.string.player2))
                disableButtonsMatrix()
            } else if (it.player1 == it.player2 && it.player1 == 3) {
                showWinDialog(null)
                disableButtonsMatrix()
            }
        }
    }
    private fun setSettingsObserves(){
        settingsViewModel.setting.observe(this) {
            val listCategories = mutableListOf<Int>()
            difficult = it.difficulty
            if (it.art) {
                listCategories.add(Settings.ART_CODE)
            }
            if (it.animals) {
                listCategories.add(Settings.ANIMALS_CODE)
            }
            if (it.films) {
                listCategories.add(Settings.FILMS_CODE)
            }
            if (it.generalKnowledge) {
                listCategories.add(Settings.GENERAL_KNOWLEDGE_CODE)
            }
            if (it.geography) {
                listCategories.add(Settings.GEOGRAPHY_CODE)
            }
            if (it.history) {
                listCategories.add(Settings.HISTORY_CODE)
            }
            if (it.mathematics) {
                listCategories.add(Settings.MATHEMATICS_CODE)
            }
            if (it.scienceNature) {
                listCategories.add(Settings.SCIENCE_NATURE_CODE)
            }
            categories = listCategories
        }

    }

    private fun setQuestionObserves(){
        questionViewModel.question.observe(this) {
            showQuestion(it)
        }

        questionViewModel.isCorrectAnswer.observe(this) {
            gameViewModel.makeMove(selectedLine, selectedColumn, it)
            if (it) {
                showSnackbar(getString(R.string.right_answer_message))
            } else {
                showSnackbar(getString(R.string.wrong_answer_message))
            }
        }
    }

    private fun settingMatrix(){
        newGameBinding.matrix.position00.setOnClickListener {
            selectedLine = 0
            selectedColumn = 0
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
        }
        newGameBinding.matrix.position01.setOnClickListener {
            selectedLine = 0
            selectedColumn = 1
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
        }
        newGameBinding.matrix.position02.setOnClickListener {
            selectedLine = 0
            selectedColumn = 2
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
        }
        newGameBinding.matrix.position10.setOnClickListener {
            selectedLine = 1
            selectedColumn = 0
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
        }
        newGameBinding.matrix.position11.setOnClickListener {
            selectedLine = 1
            selectedColumn = 1
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
        }
        newGameBinding.matrix.position12.setOnClickListener {
            selectedLine = 1
            selectedColumn = 2
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
        }
        newGameBinding.matrix.position20.setOnClickListener {
            selectedLine = 2
            selectedColumn = 0
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
        }
        newGameBinding.matrix.position21.setOnClickListener {
            selectedLine = 2
            selectedColumn = 1
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
        }
        newGameBinding.matrix.position22.setOnClickListener {
            selectedLine = 2
            selectedColumn = 2
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
        }
    }

    private fun updateMatrix(board: Array<Array<String>>){
        setImageButtonMatrix(newGameBinding.matrix.position00, board[0][0])
        setImageButtonMatrix(newGameBinding.matrix.position01, board[0][1])
        setImageButtonMatrix(newGameBinding.matrix.position02, board[0][2])
        setImageButtonMatrix(newGameBinding.matrix.position10, board[1][0])
        setImageButtonMatrix(newGameBinding.matrix.position11, board[1][1])
        setImageButtonMatrix(newGameBinding.matrix.position12, board[1][2])
        setImageButtonMatrix(newGameBinding.matrix.position20, board[2][0])
        setImageButtonMatrix(newGameBinding.matrix.position21, board[2][1])
        setImageButtonMatrix(newGameBinding.matrix.position22, board[2][2])
    }
    private fun setImageButtonMatrix(imgButton: ImageButton, symbol: String){
        when (symbol) {
            TicTacToe.PLAYER_1_SYMBOL -> {
                imgButton.setImageResource(R.drawable.ic_cross)
                imgButton.isEnabled = false
            }
            TicTacToe.PLAYER_2_SYMBOL -> {
                imgButton.setImageResource(R.drawable.ic_circle)
                imgButton.isEnabled = false
            }
            else -> {
                imgButton.setImageResource(android.R.color.transparent)
                imgButton.isEnabled = true
            }
        }

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

    private fun showWinDialog(winnerName: String?) {
        if(winnerName!=null){
            AlertDialog.Builder(this)
                .setTitle("Parabéns $winnerName!")
                .setMessage("Você venceu a partida!")
                .setPositiveButton(getString(R.string.close_button)
                ) { dialog, _ ->
                    finish()
                    dialog?.dismiss()
                }
                .show()
        }else{
            AlertDialog.Builder(this)
                .setTitle("Empate!")
                .setMessage("Nunhum jogador venceu!")
                .setPositiveButton(getString(R.string.close_button)
                ) { dialog, _ ->
                    finish()
                    dialog?.dismiss()
                }
                .show()
        }
    }

    private fun showQuestion(question : Question) {
        val alternatives = mutableListOf<String>()
        alternatives.add(question.correct_answer)
        question.incorrect_answers.forEach{
                element ->
            alternatives.add(element)
        }

        alternatives.shuffle()
        val radioGroup = RadioGroup(applicationContext)

        alternatives.forEachIndexed { index, text ->
            val radioButton = RadioButton(applicationContext)
            radioButton.text = text
            radioButton.id = index
            radioGroup.addView(radioButton)
        }
        radioGroup.setPadding(16,8,16,8)

        var isRadioButtonSelected = false

        radioGroup.setOnCheckedChangeListener { _, _ ->
            isRadioButtonSelected = true

        }
        val builder = AlertDialog.Builder(this)
            .setTitle("Question")
            .setMessage(question.quetion)
            .setView(radioGroup)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.submit_button)) { _, _ ->}
            .create()

        builder.setOnShowListener {
            builder.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if(isRadioButtonSelected){
                    val selectedRadioButtonId = radioGroup.checkedRadioButtonId
                    val resposta = alternatives[selectedRadioButtonId]
                    questionViewModel.checkAnswer(resposta)
//                    Toast.makeText(this, "Opção selecionada: ${alternatives[selectedRadioButtonId]}", Toast.LENGTH_SHORT).show()
                    builder.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun showSnackbar(message: String){
        Snackbar.make(
            newGameBinding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

}