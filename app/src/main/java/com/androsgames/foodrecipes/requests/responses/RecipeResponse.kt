package com.androsgames.foodrecipes.requests.responses

import android.support.annotation.Nullable
import com.androsgames.foodrecipes.models.Recipe
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RecipeResponse (

    @SerializedName ("recipe")
    @Expose
    private var recipe: Recipe?,

    @SerializedName("error")
    @Expose
    private var error : String? = ""

){
    fun getRecipe(): Recipe? {
        return recipe
    }

    @Nullable
    fun getError() : String? {
        return error
    }


}