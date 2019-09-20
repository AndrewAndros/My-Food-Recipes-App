package com.androsgames.foodrecipes.util

import android.arch.lifecycle.LiveData
import com.androsgames.foodrecipes.requests.responses.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class LiveDataCallAdapter<R>(private var responseType: Type) : CallAdapter<R, LiveData<ApiResponse<R>>> {

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {
                  return object : LiveData<ApiResponse<R>>() {
                      override fun onActive() {
                          super.onActive()
                          val apiResponse : ApiResponse<R> = ApiResponse()
                          call.enqueue(object : Callback<R> {
                              override fun onFailure(call: Call<R>, t: Throwable) {
                                  postValue(apiResponse.create(t))
                              }

                              override fun onResponse(call: Call<R>, response: Response<R>) {
                                  postValue(apiResponse.create(response))
                              }

                          })
                      }

                  }
    }

    override fun responseType(): Type {
        return responseType
    }


}