package com.androsgames.foodrecipes.requests.responses

import android.support.annotation.Nullable
import com.androsgames.foodrecipes.models.Recipe
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

 class RecipeSearchResponse (

    @SerializedName("count")
    @Expose
    private var count : Int = 0,

    @SerializedName("recipes")
    @Expose
    private var recipes : List<Recipe>?,

    @SerializedName("error")
    @Expose
    private var error : String? = ""
){

     fun getCount() : Int {
        return  count
    }

     @Nullable
     fun getError() : String? {
        return error
     }


     @Nullable
    fun getRecipes() : List<Recipe>? {
        return recipes
    }


}