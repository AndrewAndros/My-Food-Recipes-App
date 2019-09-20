package com.androsgames.foodrecipes.persistence

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.androsgames.foodrecipes.models.Recipe


@Dao
interface RecipeDao {

    @Insert(onConflict = IGNORE)
    fun insertRecipes(vararg recipe : Recipe) : LongArray

    @Insert(onConflict = REPLACE)
    fun insertRecipe(recipe: Recipe)

    @Query("UPDATE recipes SET title = :title, publisher = :publisher, image_url = :image_url, source_url = :source_url, social_rank = :social_rank, bookmark = :bookmark WHERE recipe_id = :recipe_id ")
    fun updateRecipe(recipe_id : String, title : String, publisher : String, image_url : String, source_url : String, social_rank : Float, bookmark : Int)

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%' ORDER BY social_rank DESC LIMIT (:pageNumber * 30)")
    fun searchRecipes(query : String, pageNumber : Int) : LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE recipe_id = :recipe_id")
    fun getRecipe(recipe_id: String) : LiveData<Recipe>

    @Query("SELECT * FROM recipes WHERE bookmark = 1 ORDER BY social_rank DESC")
    fun getBookmarkedRecipes() : LiveData<List<Recipe>>


}