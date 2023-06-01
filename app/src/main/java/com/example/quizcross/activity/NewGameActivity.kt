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
import com.example.quizcross.model.TicTacToe
import com.example.quizcross.repository.QuestionRepository
import com.example.quizcross.repository.SettingRepository
import com.example.quizcross.service.AppConstants
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
    private var difficult: String = AppConstants.SETTINGS.MEDIUM_DIFFICULTY
    private var player1Name: String = ""
    private var player2Name: String = ""
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
        player1Name = getString(R.string.player1)
        player2Name = getString(R.string.player2)

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
            SettingsViewModelFactory(application, repository)
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
                showSnackbar(player1Name + " " + getString(R.string.win))
            }
        }

        gameViewModel.isPlayer2Winner.observe(this) {
            if (it) {
                gameViewModel.newGame()
                resetMatrix()
                showSnackbar(player2Name + " " + getString(R.string.win))
            }
        }

        gameViewModel.ticTacToe.observe(this){
            if(it.isPlayer1Turn){
                val turnOrder = getString(R.string.turn) + " " + player1Name
                newGameBinding.textTurn.text = turnOrder
            }else{
                val turnOrder = getString(R.string.turn) +" "+ player2Name
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
                showWinDialog(player1Name)
                disableButtonsMatrix()
            } else if (it.player2 == 3 && it.player1 != 3) {
                showWinDialog(player2Name)
                disableButtonsMatrix()
            } else if (it.player1 == it.player2 && it.player1 == 3) {
                showWinDialog(null)
                disableButtonsMatrix()
            }
        }
    }
    private fun setSettingsObserves(){
        settingsViewModel.loadSettingsProcess.observe(this) {
            val listCategories = mutableListOf<Int>()
            when(it){
                is ResultModel.Success -> {
                    player1Name = it.data.player1Name
                    player2Name = it.data.player2Name
                    newGameBinding.textPlayer1Name.text = player1Name
                    newGameBinding.textPlayer2Name.text = player2Name
                    difficult = it.data.difficulty
                    if (it.data.art) {
                        listCategories.add(AppConstants.SETTINGS.ART_CODE)
                    }
                    if (it.data.animals) {
                        listCategories.add(AppConstants.SETTINGS.ANIMALS_CODE)
                    }
                    if (it.data.films) {
                        listCategories.add(AppConstants.SETTINGS.FILMS_CODE)
                    }
                    if (it.data.generalKnowledge) {
                        listCategories.add(AppConstants.SETTINGS.GENERAL_KNOWLEDGE_CODE)
                    }
                    if (it.data.geography) {
                        listCategories.add(AppConstants.SETTINGS.GEOGRAPHY_CODE)
                    }
                    if (it.data.history) {
                        listCategories.add(AppConstants.SETTINGS.HISTORY_CODE)
                    }
                    if (it.data.mathematics) {
                        listCategories.add(AppConstants.SETTINGS.MATHEMATICS_CODE)
                    }
                    if (it.data.scienceNature) {
                        listCategories.add(AppConstants.SETTINGS.SCIENCE_NATURE_CODE)
                    }
                    if(listCategories.size > 0){
                        categories = listCategories
                    }else{
                        showSnackbar(getString(R.string.unexpected_error))
                        finish()
                    }
                }
                is ResultModel.Error -> {
                    showSnackbar(getString(R.string.unexpected_error))
                    finish()
                }
                is ResultModel.Loading -> {}
            }

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