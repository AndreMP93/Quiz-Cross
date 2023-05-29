package com.example.quizcross.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.R
import com.example.quizcross.databinding.ActivityNewGameBinding
import com.example.quizcross.model.Question
import com.example.quizcross.model.ResultModel
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
    private lateinit var progressDialog: ProgressDialog
    private var boardWidget: MutableList<MutableList<ImageButton>> = mutableListOf(mutableListOf(),
        mutableListOf(), mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newGameBinding = ActivityNewGameBinding.inflate(layoutInflater)
        setContentView(newGameBinding.root)

        boardWidget[0].add(newGameBinding.matrix.position00)
        boardWidget[0].add(newGameBinding.matrix.position01)
        boardWidget[0].add(newGameBinding.matrix.position02)
        boardWidget[1].add(newGameBinding.matrix.position10)
        boardWidget[1].add(newGameBinding.matrix.position11)
        boardWidget[1].add(newGameBinding.matrix.position12)
        boardWidget[2].add(newGameBinding.matrix.position20)
        boardWidget[2].add(newGameBinding.matrix.position21)
        boardWidget[2].add(newGameBinding.matrix.position22)
        progressDialog = ProgressDialog(this)
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
                val turnOrder = getString(R.string.turn) + " " + getString(R.string.player1)
                newGameBinding.textTurn.text = turnOrder
            }else{
                val turnOrder = getString(R.string.turn) +" "+ getString(R.string.player2)
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
        questionViewModel.loadQuestionProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    progressDialog.dismiss()
                    showQuestion(it.data)
                }
                is ResultModel.Error -> {
                }
                is ResultModel.Loading -> {
                    progressDialog.setMessage("Loading...")
                    progressDialog.show()
                }
            }
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
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val layoutParams = newGameBinding.matrix.position00.layoutParams
        layoutParams.width = (screenWidth * 0.3).toInt()
        layoutParams.height = (screenWidth * 0.3).toInt()

        for (line in boardWidget){
            for (imageButton in line){
                imageButton.layoutParams = layoutParams
                imageButton.setOnClickListener {
                    selectedLine = boardWidget.indexOf(line)
                    selectedColumn = line.indexOf(imageButton)
                    questionViewModel.getQuestion(category = categories.random(), difficult = difficult)
                }
            }
        }
    }

    private fun updateMatrix(board: Array<Array<String>>){
        for (line in boardWidget){
            for (imageButton in line){
                setImageButtonMatrix(imageButton, board[boardWidget.indexOf(line)][line.indexOf(imageButton)])
            }
        }
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
        for (line in boardWidget){
            for (imageButton in line){
                imageButton.isEnabled = true
                imageButton.setImageResource(android.R.color.transparent)
            }
        }
    }
    private fun disableButtonsMatrix(){
        for (line in boardWidget){
            for (imageButton in line){
                imageButton.isEnabled = false
            }
        }
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
            radioButton.setTextColor(ContextCompat.getColor(this, R.color.button_color))
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
            .setMessage(question.question)
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