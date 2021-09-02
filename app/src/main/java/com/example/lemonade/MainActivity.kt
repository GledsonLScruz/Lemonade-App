package com.example.lemonade

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


//TAGS for savedInstance Bundle
const val LEMONADE_STATE = "LEMONADE_STATE"
const val LEMON_SIZE = "LEMON_SIZE"
const val SQUEEZE_COUNT = "SQUEEZE_COUNT"

class MainActivity : AppCompatActivity() {

    // SELECT represents the "pick lemon" state
    private val SELECT = "select"
    // SQUEEZE represents the "squeeze lemon" state
    private val SQUEEZE = "squeeze"
    // DRINK represents the "drink lemonade" state
    private val DRINK = "drink"
    // RESTART represents the state where the lemonade has been drunk and the glass is empty
    private val RESTART = "restart"

    // Define the default state to select
    private var lemonadeState = "select"
    // Default lemonSize to -1
    private var lemonSize = -1
    // Default the squeezeCount to -1
    private var squeezeCount = -1

    //Declare animations
    lateinit private var stb: Animation
    lateinit private var leftin: Animation
    lateinit private var leftout: Animation
    lateinit private var squeeze: Animation

    private var lemonTree = LemonTree()
    private var lemonImage: ImageView? = null


    private fun loadAnimations(){
        stb = AnimationUtils.loadAnimation(this,R.anim.stb)
        leftin = AnimationUtils.loadAnimation(this,R.anim.leftin)
        leftout = AnimationUtils.loadAnimation(this,R.anim.leftout)
        squeeze = AnimationUtils.loadAnimation(this,R.anim.squeeze)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadAnimations()

        // === DO NOT ALTER THE CODE IN THE FOLLOWING IF STATEMENT ===
        if (savedInstanceState != null) {
            lemonadeState = savedInstanceState.getString(LEMONADE_STATE, "select")
            lemonSize = savedInstanceState.getInt(LEMON_SIZE, -1)
            squeezeCount = savedInstanceState.getInt(SQUEEZE_COUNT, -1)
        }
        // === END IF STATEMENT ===

        lemonImage = findViewById<ImageView>(R.id.image_lemon_state)
        //a animação
        lemonImage?.startAnimation(stb)
        lemonImage?.setOnClickListener {
            clickLemonImage()
        }
        updateLemonImage()

        lemonImage?.setOnLongClickListener {
            showSnackbar()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(LEMONADE_STATE, lemonadeState)
        outState.putInt(LEMON_SIZE, lemonSize)
        outState.putInt(SQUEEZE_COUNT, squeezeCount)
        super.onSaveInstanceState(outState)
    }


    private fun clickLemonImage() {

        when (lemonadeState) {
            SELECT -> {
                lemonadeState = SQUEEZE
                lemonSize = lemonTree.pick()
                squeezeCount = 0
            }
            SQUEEZE -> {
                lemonImage?.startAnimation(squeeze)
                squeezeCount += 1
                lemonSize -= 1
                if (lemonSize == 0){
                    lemonadeState = DRINK
                }
            }
            DRINK -> {
                lemonImage?.startAnimation(leftin)
                lemonadeState = RESTART
                lemonSize = -1
            }
            RESTART -> {
                lemonadeState = SELECT
            }
        }
        updateLemonImage()
    }


    private fun updateLemonImage() {
        val textAction = findViewById<TextView>(R.id.text_action)

        when (lemonadeState) {
            SELECT -> {textAction.setText(R.string.lemon_select)
                lemonImage?.setImageResource(R.drawable.lemon_tree)}
            SQUEEZE -> {textAction.setText(R.string.lemon_squeeze)
                lemonImage?.setImageResource(R.drawable.lemon_squeeze)}
            DRINK -> {textAction.setText(R.string.lemon_drink)
                lemonImage?.setImageResource(R.drawable.lemon_drink)}
            RESTART -> {textAction.setText(R.string.lemon_empty_glass)
                lemonImage?.setImageResource(R.drawable.lemon_restart)}
        }
    }

    private fun showSnackbar(): Boolean {
        if (lemonadeState != SQUEEZE) {
            return false
        }
        val squeezeText = getString(R.string.squeeze_count, squeezeCount)
        Snackbar.make(
            findViewById(R.id.constraint_Layout),
            squeezeText,
            Snackbar.LENGTH_SHORT
        ).show()
        return true
    }
}

class LemonTree {
    fun pick(): Int {
        return (2..4).random()
    }
}
