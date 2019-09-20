package com.androsgames.foodrecipes.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.androsgames.foodrecipes.models.Recipe
import com.androsgames.foodrecipes.repositories.RecipeRepository
import com.androsgames.foodrecipes.util.Resource

class RecipeViewModel (application: Application) : AndroidViewModel(Application()) {

    private lateinit var mRecipeId : String
    private var mDidRetrievedRecipe : Boolean = false
    private var recipeRepository : RecipeRepository = RecipeRepository.getInstance(application)




    fun getRecipeId() : String {
        return mRecipeId
    }



    fun searchRecipeApi(recipeId: String) : LiveData<Resource<Recipe>> {
         return recipeRepository.searchRecipeApi(recipeId)
    }

    fun setRetrievedRecipe (retrievedRecipe : Boolean) {
        mDidRetrievedRecipe = retrievedRecipe
    }

    fun didRetrievedRecipe() : Boolean {
        return mDidRetrievedRecipe
    }
}