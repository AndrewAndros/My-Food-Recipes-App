package com.androsgames.foodrecipes

import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.androsgames.foodrecipes.databinding.ActivityBaseBinding

abstract class  BaseActivity : AppCompatActivity() {


lateinit var binding: ActivityBaseBinding
    override fun setContentView(layoutResID: Int) {


        binding=ActivityBaseBinding.inflate(layoutInflater)

        layoutInflater.inflate(layoutResID, binding.activityContent, true)
    }

    fun showProgressBar (visibility : Boolean) {
        binding.progressBar.visibility = if(visibility) View.VISIBLE else View.GONE
    }


}