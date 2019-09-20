package com.androsgames.foodrecipes.util

class Constants {


    companion object {

        const val BASE_URL : String = "https://www.food2fork.com"

        const val API_KEY : String = "9d8b699add692a3c2f222d92a888e253"


//        first one   abf8a81365b1aae342cecf5b1b968bc2
//        second one  9d8b699add692a3c2f222d92a888e253


        val CONNECTION_TIMEOUT : Long = 10000 // 10 seconds
        val READ_TIMEOUT : Long = 3000 // 3 seconds
        val WRITE_TIMEOUT : Long = 3000 // 3 seconds

        val RECIPE_REFRESH_TIME : Int = 60 * 60 * 24 * 30 // 30 days (in seconds)

        val DEFAULT_SEARCH_CATEGORIES =
            arrayOf("Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian", "Bookmarks")

        val DEFAULT_SEARCH_CATEGORY_IMAGES =
            arrayOf("barbeque", "breakfast", "chicken", "beef", "brunch", "dinner", "wine", "italian", "favorite")
    }




}