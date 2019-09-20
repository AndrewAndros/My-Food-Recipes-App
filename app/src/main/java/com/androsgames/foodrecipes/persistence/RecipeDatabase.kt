package com.androsgames.foodrecipes.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.androsgames.foodrecipes.models.Recipe

@Database(entities = arrayOf(Recipe::class), version = 1)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {



    companion object {
        val DATABASE_NAME : String = "recipes_db"
        private var instance : RecipeDatabase? = null

        fun getInstance(context : Context) : RecipeDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, RecipeDatabase::class.java, DATABASE_NAME).build()
            }
            return instance
        }
    }

    abstract fun recipeDAO() : RecipeDao


}