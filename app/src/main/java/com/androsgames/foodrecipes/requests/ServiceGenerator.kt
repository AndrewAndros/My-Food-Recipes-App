package com.androsgames.foodrecipes.requests

import com.androsgames.foodrecipes.util.Constants
import com.androsgames.foodrecipes.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {

        private val client  = OkHttpClient.Builder()
            // establish connection to server
            .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)

            // time between each byte read from server
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.MILLISECONDS)

            // time between each byte sent to server
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.MILLISECONDS)

            .retryOnConnectionFailure(false)

            .build()


        val retrofitBuilder: Retrofit.Builder by lazy {
            Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
        }


        val recipeApi : RecipeApi by lazy {
            retrofitBuilder
                .build()
                .create(RecipeApi::class.java)
        }
 }


