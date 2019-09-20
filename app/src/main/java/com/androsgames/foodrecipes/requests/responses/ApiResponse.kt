package com.androsgames.foodrecipes.requests.responses

import retrofit2.Response
import java.io.IOException


open class ApiResponse <T> {


    fun create(error: Throwable): ApiResponse<T> {
        return ApiErrorResponse(
            (if (error.message != "") error.message else "Unknown error\nCheck network connection").toString()
        )
    }

    fun create (response : Response<T>) : ApiResponse<T> {
        if (response.isSuccessful)  {
            val body: T? = response.body()

            if (body is RecipeSearchResponse) {
                if(!CheckRecipeApiKey.isRecipeApiKeyValid(body as RecipeSearchResponse)) {
                    val errorMessage = "Api key is invalid or expired"
                    return ApiErrorResponse(errorMessage)
                }
            }

            if (body is RecipeResponse) {
                if(!CheckRecipeApiKey.isRecipeApiKeyValid(body as RecipeResponse)) {
                    val errorMessage = "Api key is invalid or expired"
                    return ApiErrorResponse(errorMessage)
                }
            }

            if(body == null || response.code() == 204) { // 204 is empty response code
                return ApiEmptyResponse()
            } else {
                return ApiSuccessResponse(body)
            }
        } else  {
            var errorMsg: String = ""
            try {
                errorMsg = response.errorBody().toString()
            } catch (e: IOException) {
                e.printStackTrace()
                errorMsg = response.message()
            }
            return ApiErrorResponse(errorMsg)
        }
    }

    class ApiSuccessResponse<T>(private var body: T) : ApiResponse<T>() {

        fun getBody() : T {
            return  body
        }
    }

    class ApiErrorResponse<T>(private var errorMessage : String) : ApiResponse<T>() {

        fun getErrorMessage() : String {
            return  errorMessage
        }
    }

    class ApiEmptyResponse<T> : ApiResponse<T>()

}