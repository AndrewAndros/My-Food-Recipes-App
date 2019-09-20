package com.androsgames.foodrecipes.requests

import android.arch.lifecycle.LiveData
import com.androsgames.foodrecipes.requests.responses.ApiResponse
import com.androsgames.foodrecipes.requests.responses.RecipeResponse
import com.androsgames.foodrecipes.requests.responses.RecipeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {

    @GET("api/search")
    fun searchRecipes (
            @Query("key") key : String,
            @Query("q")  query : String,
            @Query("page") page : String) : LiveData<ApiResponse<RecipeSearchResponse>>

    @GET("api/get")
     fun getRecipe (
        @Query("key") key : String,
        @Query("rId") recipeId : String
    ) : LiveData<ApiResponse<RecipeResponse>>


}