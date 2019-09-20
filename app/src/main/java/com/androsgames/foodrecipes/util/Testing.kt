package com.androsgames.foodrecipes.util

import android.util.Log
import com.androsgames.foodrecipes.models.Recipe

class Testing {
    companion object {
        fun printRecipes (tag : String, recipes : List<Recipe>) {
            for (recipe : Recipe in recipes) {
                Log.d(tag, "printRecipes ${recipe.recipe_id}, ${recipe.title}")
            }
        }
    }
}