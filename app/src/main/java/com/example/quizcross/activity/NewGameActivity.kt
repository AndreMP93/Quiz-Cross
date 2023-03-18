package com.example.quizcross.activity

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quizcross.R
import com.example.quizcross.databinding.ActivityNewGameBinding
import com.example.quizcross.model.Question
import com.example.quizcross.model.Settings
import com.example.quizcross.repository.QuestionRepository
import com.example.quizcross.repository.SettingRepository
import com.example.quizcross.viewmodel.GameViewModel
import com.example.quizcross.viewmodel.QuestionViewModel
import com.example.quizcross.viewmodel.SettingsViewModel
import com.example.quizcross.viewmodel.factory.QuestionViewModelFactory
import com.example.quizcross.viewmodel.factory.SettingsViewModelFactory
import com.google.android.material.snackbar.Snackbar

class NewGameActivity : AppCompatActivity() {

    private lateinit var newGameBinding: ActivityNewGameBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var difficult: String
    private lateinit var categories: MutableList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newGameBinding = ActivityNewGameBinding.inflate(layoutInflater)
        setContentView(newGameBinding.root)


        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val repository = SettingRepository(sharedPreferences)
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(repository)
        ).get(SettingsViewModel::class.java)

        val questionRepository = QuestionRepository()
        questionViewModel = ViewModelProvider(
            this,
            QuestionViewModelFactory(questionRepository)
        ).get(QuestionViewModel::class.java)

        setSettingsObserves()
        setQuestionObserves()
        settingsViewModel.getSettings()
        settingMatrix()

        gameViewModel.isPlayer1Winner.observe(this, Observer {
            if (it){
                gameViewModel.newGame()
                resetMatrix()
                Snackbar.make(
                    newGameBinding.root,
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
                newGameBinding.textTurn.text = "Turn: Player 1"
            }else{
                newGameBinding.textTurn.text = "Turn: Player 2"
            }
            if(it.isTie){
                gameViewModel.newGame()
                resetMatrix()

            }
        })

        gameViewModel.scoreboard.observe(this, Observer {
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

    private fun setSettingsObserves(){
        settingsViewModel.setting.observe(this, Observer {
            val listCategories = mutableListOf<Int>()
            difficult = it.difficulty
            if(it.art){
                listCategories.add(Settings.ART_CODE)
            }
            if(it.animals){
                listCategories.add(Settings.ANIMALS_CODE)
            }
            if(it.films){
                listCategories.add(Settings.FILMS_CODE)
            }
            if(it.generalKnowledge){
                listCategories.add(Settings.GENERAL_KNOWLEDGE_CODE)
            }
            if(it.geography){
                listCategories.add(Settings.GEOGRAPHY_CODE)
            }
            if(it.history){
                listCategories.add(Settings.HISTORY_CODE)
            }
            if(it.mathematics){
                listCategories.add(Settings.MATHEMATICS_CODE)
            }
            if(it.scienceNature){
                listCategories.add(Settings.SCIENCE_NATURE_CODE)
            }
            categories = listCategories
        })

    }

    private fun setQuestionObserves(){
        questionViewModel.question.observe(this, Observer {
            showQuestion(it)
        })
    }

    private fun settingMatrix(){
        newGameBinding.matrix.position00.setOnClickListener {
            questionViewModel.getQuestion(category = categories.random(), difficult = difficult)

            gameViewModel.makeMove(0,0)
            setImageButtonMatrix(newGameBinding.matrix.position00)
        }
        newGameBinding.matrix.position01.setOnClickListener {
            gameViewModel.makeMove(0,1)
            setImageButtonMatrix(newGameBinding.matrix.position01)
        }
        newGameBinding.matrix.position02.setOnClickListener {
            gameViewModel.makeMove(0,2)
            setImageButtonMatrix(newGameBinding.matrix.position02)
        }
        newGameBinding.matrix.position10.setOnClickListener {
            gameViewModel.makeMove(1,0)
            setImageButtonMatrix(newGameBinding.matrix.position10)
        }
        newGameBinding.matrix.position11.setOnClickListener {
            gameViewModel.makeMove(1,1)
            setImageButtonMatrix(newGameBinding.matrix.position11)
        }
        newGameBinding.matrix.position12.setOnClickListener {
            gameViewModel.makeMove(1,2)
            setImageButtonMatrix(newGameBinding.matrix.position12)
        }
        newGameBinding.matrix.position20.setOnClickListener {
            gameViewModel.makeMove(2,0)
            setImageButtonMatrix(newGameBinding.matrix.position20)
        }
        newGameBinding.matrix.position21.setOnClickListener {
            gameViewModel.makeMove(2,1)
            setImageButtonMatrix(newGameBinding.matrix.position21)
        }
        newGameBinding.matrix.position22.setOnClickListener {
            gameViewModel.makeMove(2,2)
            setImageButtonMatrix(newGameBinding.matrix.position22)
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
                        gameViewModel.newGame()
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
                        gameViewModel.newGame()
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
        radioGroup.setPadding(8,8,8,8)

        var isRadioButtonSelected = false // Adiciona a variável no escopo da função

        radioGroup.setOnCheckedChangeListener { _, _ ->
            // Altera a variável para true quando um RadioButton for selecionado
            isRadioButtonSelected = true
        }
        // Cria o AlertDialog
        val builder = AlertDialog.Builder(this)
            .setTitle("Question")
            .setMessage(question.quetion)
            .setView(radioGroup)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.submit_button)) { dialog, which ->
                // Valida se um RadioButton foi selecionado antes de fechar o AlertDialog
                if(isRadioButtonSelected){
                    // Recupera o RadioButton selecionado
                    val selectedRadioButtonId = radioGroup.checkedRadioButtonId
                    val resposta = alternatives[selectedRadioButtonId]
                    questionViewModel.checkAnswer(resposta)
                    Toast.makeText(this, "Opção selecionada: ${alternatives[selectedRadioButtonId]}", Toast.LENGTH_SHORT).show()
                }
            }
            .create()
        // Adiciona a verificação do RadioButton ao botão positivo
        builder.setOnShowListener {
            builder.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if (isRadioButtonSelected) {
                    builder.dismiss()
                }
            }
        }
        // Exibe o AlertDialog
        builder.show()
    }



}