package com.example.rockpaperscissor

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    // Global variable for the userScore and appScore
    private var userScore: Int = 0
    private var appScore: Int = 0
    private var tries: Int = 0
    private var highScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val choices = arrayOf("paper", "rock", "scissor")
        val paperIcon = findViewById<ImageView>(R.id.imageView)
        val rockIcon = findViewById<ImageView>(R.id.imageView2)
        val scissorIcon = findViewById<ImageView>(R.id.imageView3)
        val appIcon = findViewById<ImageView>(R.id.imageView4)
        val userIcon = findViewById<ImageView>(R.id.imageView5)
        val result = findViewById<TextView>(R.id.textView8)
        val userMarks = findViewById<TextView>(R.id.textView6)
        val appMarks = findViewById<TextView>(R.id.textView7)
        val reset = findViewById<Button>(R.id.button)

        // initialize the tryAgain dialog box
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.setCancelable(false)


        // this function execute when the player click the paper,scissor,rock buttons
        fun playGame(playerChoice: String) {
            // get the app chose using the random library and the choice array
            val appChoice = choices[(0..2).random()]

            println("app:  $appChoice")
            println("your:  $playerChoice")

            // Change the player put image according to the player choice
            when (playerChoice) {
                "rock" -> userIcon.setImageResource(R.drawable.rock_removebg_preview)
                "paper" -> userIcon.setImageResource(R.drawable.paper_removebg_preview)
                "scissor" -> userIcon.setImageResource(R.drawable.scissor_removebg_preview)
            }

            // Change the app put image according to the app choice
            when (appChoice) {
                "rock" -> appIcon.setImageResource(R.drawable.rock_removebg_preview)
                "paper" -> appIcon.setImageResource(R.drawable.paper_removebg_preview)
                "scissor" -> appIcon.setImageResource(R.drawable.scissor_removebg_preview)
            }

            // change and view the player current condition of his playing
            if (playerChoice == appChoice) {
                result.text = "IT'S TIE"
            } else {
                when (playerChoice) {
                    "rock" -> result.text = if (appChoice === "scissor") "YOU WIN" else "YOU LOSE"
                    "paper" -> result.text = if (appChoice === "rock") "YOU WIN" else "YOU LOSE"
                    "scissor" -> result.text = if (appChoice === "paper") "YOU WIN" else "YOU LOSE"
                }
            }

            // calculate the payer score and the app score and also change the player state text
            when (result.text) {
                "YOU WIN" -> {
                    result.setTextColor(Color.parseColor("#008000"))
                    userScore++
                    userMarks.text = userScore.toString()
                    /**** Vibration effect added for the "YOU WIN" state ****/
                    // get the VIBRATOR_SERVICE system service
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // creates the vibration of default amplitude for 80ms
                        val vibrationEffect1 =
                            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                        // cancel other vibrations currently taking place
                        vibrator.cancel()
                        vibrator.vibrate(vibrationEffect1)
                    }
                }

                "YOU LOSE" -> {
                    result.setTextColor(Color.parseColor("#FF0000"))
                    appScore++
                    tries++
                    appMarks.text = appScore.toString()
                    if (tries == 3) {
                        println("You Lose")
                        if (userScore > highScore) {
                            highScore = userScore
                        }
                        val hScore = dialog.findViewById<TextView>(R.id.textView9)
                        hScore.text = highScore.toString()
                        println(highScore)
                        dialog.show()
                    }
                }

                "IT'S TIE" -> {
                    result.setTextColor(Color.parseColor("#000000"))
                }
            }
        }

        /*This function will reset all game it will reset score,and player state text and the player icon and app icon*/
        fun reset() {
            tries = 0
            appScore = 0
            userScore = 0
            appMarks.text = "0"
            userMarks.text = "0"
            result.text = " "
            userIcon.setImageResource(R.drawable.you_removebg_preview)
            appIcon.setImageResource(R.drawable.app_removebg_preview)
        }

        // Event listener for paper button
        paperIcon.setOnClickListener {
            playGame("paper")
        }
        // Event listener for rock button
        rockIcon.setOnClickListener {
            playGame("rock")
        }
        // Event listener for scissor button
        scissorIcon.setOnClickListener {
            playGame("scissor")
        }
        // Event listener for tryAgain button
        val tryAgain = dialog.findViewById<Button>(R.id.button3)
        tryAgain.setOnClickListener {
            dialog.dismiss()
            reset()
        }
        // Event listener for rest button
        reset.setOnClickListener {
            reset()
        }

    }

    // This function save the all appScore and userScore
    private fun saveScore(userScore: Int, appScore: Int, highScore: Int) {
        val sharedPref = this.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putInt("userScore", userScore)
        editor?.putInt("appScore", appScore)
        editor?.putInt("highScore", highScore)
        editor?.apply()
    }

    // This function retire the saved appScore and userScore
    private fun getScore() {
        val sharedPref = this.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        userScore = sharedPref.getInt("userScore", 0)
        findViewById<TextView>(R.id.textView6).text = userScore.toString()
        appScore = sharedPref.getInt("appScore", 0)
        findViewById<TextView>(R.id.textView7).text = appScore.toString()
        highScore = sharedPref.getInt("highScore", 0)
    }

    // on app launching the this function assign all retired data to app
    override fun onResume() {
        super.onResume()
        getScore()
    }

    // on app closing the this function save all playerScore and appScore
    override fun onPause() {
        super.onPause()
        saveScore(userScore, appScore, highScore)
    }
}