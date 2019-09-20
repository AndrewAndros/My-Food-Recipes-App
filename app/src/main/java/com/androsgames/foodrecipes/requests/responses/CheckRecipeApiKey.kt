package com.androsgames.foodrecipes.requests.responses

sealed class CheckRecipeApiKey {

    companion object {
        fun isRecipeApiKeyValid(response: RecipeSearchResponse): Boolean {
            return response.getError() == null
        }

        fun isRecipeApiKeyValid(response: RecipeResponse): Boolean {
            return response.getError() == null
        }


    }
}