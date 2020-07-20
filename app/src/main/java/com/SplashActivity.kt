package com

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.androsgames.foodrecipes.R
import com.androsgames.foodrecipes.RecipeListActivity

class SplashActivity : AppCompatActivity() {


    lateinit var topanim: Animation
    lateinit var bottomanim: Animation
    lateinit var txtWelcome: TextView
    lateinit var imglogo: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

//        Animations
        topanim= AnimationUtils.loadAnimation(this , R.anim.top_animation)
        bottomanim = AnimationUtils.loadAnimation(this , R.anim.bottom_animation)

//        Hook

        txtWelcome=findViewById(R.id.txtwelocome)
        imglogo= findViewById(R.id.imglogo)

        imglogo.animation=bottomanim
        txtWelcome.animation = topanim

        Handler().postDelayed({
            val startAct = Intent(this@SplashActivity, RecipeListActivity::class.java)
            startActivity(startAct)
        }, 2000)


    }
    override fun onPause() {
        super.onPause()
        finish()
    }

}
