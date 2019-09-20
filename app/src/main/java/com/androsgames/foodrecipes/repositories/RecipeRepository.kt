package com.androsgames.foodrecipes.repositories

import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import com.androsgames.foodrecipes.AppExecutors
import com.androsgames.foodrecipes.models.Recipe
import com.androsgames.foodrecipes.persistence.RecipeDao
import com.androsgames.foodrecipes.persistence.RecipeDatabase
import com.androsgames.foodrecipes.requests.ServiceGenerator
import com.androsgames.foodrecipes.requests.responses.ApiResponse
import com.androsgames.foodrecipes.requests.responses.RecipeResponse
import com.androsgames.foodrecipes.requests.responses.RecipeSearchResponse
import com.androsgames.foodrecipes.util.Constants
import com.androsgames.foodrecipes.util.NetworkBoundResource
import com.androsgames.foodrecipes.util.Resource






class RecipeRepository (context : Context, private var recipeDao : RecipeDao = RecipeDatabase.getInstance(context)!!.recipeDAO())  {


    private val TAG = "RecipeRepository"






    companion object {
        private var instance : RecipeRepository? = null
        fun getInstance(context : Context) : RecipeRepository {
            if (instance == null) {
                instance = RecipeRepository(context)
            }

            return instance as RecipeRepository
        }
    }

    fun searchRecipesApi(query : String, pageNumber : Int) : LiveData<Resource<List<Recipe>>> {
        return object :  NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.get()) {

            override fun saveCallResult(item: RecipeSearchResponse) {
               if (item.getRecipes() != null) {
                   val recipes = arrayOfNulls<Recipe>(item.getRecipes()!!.size)
                   val responseArray = item.getRecipes()!!
                //   Log.d("I TEST: ", responseArray[0].title)
                   for (a in responseArray.indices) {
                       recipes[a] = responseArray.get(a)
                       Log.d("I TEST: ", recipes[a]!!.title)
                   }
                   var index = 0
                   for (rowId : Long in recipeDao.insertRecipes(*recipes as Array<out Recipe>))   {
                       if (rowId == -1L) { // conflict detected
                           Log.d(TAG, "saveCallResult: CONFLICT... This recipe is already in cache.")
                           // if already exists, I don't want to set the ingredients or timestamp b/c they will be erased
                           recipeDao.updateRecipe(
                               recipes[index].recipe_id,
                               recipes[index].title,
                               recipes[index].publisher,
                               recipes[index].image_url,
                               recipes[index].source_url,
                               recipes[index].social_rank,
                               recipes[index].bookmark
                           )
                       }
                       index++
                   }
               }
            }

            override fun shouldFetch(data: List<Recipe>?): Boolean {
                if(query.equals("Bookmarks")) {
                    return false
                }
                    return true
            }

            override fun loadFromDb(): LiveData<List<Recipe>> {
                if(query.equals("Bookmarks")) {
                    return recipeDao.getBookmarkedRecipes()
                }
                return recipeDao.searchRecipes(query, pageNumber)
            }

            override fun createCall(): LiveData<ApiResponse<RecipeSearchResponse>> {
                return ServiceGenerator.recipeApi
                    .searchRecipes(Constants.API_KEY, query, pageNumber.toString())
            }

        }.getAsLiveData()
    }

    fun searchRecipeApi(recipeId : String) : LiveData<Resource<Recipe>> {
        return object :  NetworkBoundResource<Recipe, RecipeResponse>(AppExecutors.get()) {
            override fun saveCallResult(item: RecipeResponse) {
                // will be null if API key is expired
                if(item.getRecipe() != null) {
                      item.getRecipe()!!.timestamp = (System.currentTimeMillis() / 1000).toInt()
                      recipeDao.insertRecipe(item.getRecipe()!!)
                }
            }

            override fun shouldFetch(data: Recipe?): Boolean {
                Log.d(TAG, "shouldFetch: recipe: ${data.toString()}")
                val currentTime : Int = (System.currentTimeMillis() / 1000).toInt()
                Log.d(TAG, "shouldFetch: current time: $currentTime")
                val lastRefresh : Int = data!!.timestamp
                Log.d(TAG, "shouldFetch: last refresh: $lastRefresh")
                Log.d(TAG, "shouldFetch: it's been ${((currentTime - lastRefresh) / 60 / 60 / 24)} days since this recipe was refreshed. 30 days must elapse.")
                if((currentTime - lastRefresh) >= Constants.RECIPE_REFRESH_TIME) {
                    Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE? - YES")
                    return true
                }
                Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE? - NO")
                return false
            }

            override fun loadFromDb(): LiveData<Recipe> {
                return recipeDao.getRecipe(recipeId)
            }

            override fun createCall(): LiveData<ApiResponse<RecipeResponse>> {
               return ServiceGenerator.recipeApi.getRecipe(Constants.API_KEY, recipeId)
            }

        }.getAsLiveData()
    }

}