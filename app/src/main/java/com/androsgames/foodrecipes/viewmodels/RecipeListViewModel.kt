package com.androsgames.foodrecipes.viewmodels

import android.app.Application
import android.arch.lifecycle.*
import android.util.Log
import com.androsgames.foodrecipes.models.Recipe
import com.androsgames.foodrecipes.repositories.RecipeRepository
import com.androsgames.foodrecipes.util.Resource

class RecipeListViewModel(application: Application) : AndroidViewModel(Application()) {


    private val TAG : String = "RecipeListViewModel"

    companion object {
        val QUERY_EXHAUSTED : String = "No more results"
    }

    enum class ViewState {CATEGORIES, RECIPES}

    private var viewState : MutableLiveData<ViewState> = MutableLiveData()
    private var recipes : MediatorLiveData<Resource<List<Recipe>>> = MediatorLiveData()
    private var repository = RecipeRepository.getInstance(application)

    var isQueryExhausted : Boolean = false
    var isPerformingQuery : Boolean = false
    var pageNumber : Int = 1
    private var query : String = ""
    private var cancelRequest : Boolean = false
    private  var requestStartTime : Long = 0


    init {
        viewState.value = ViewState.CATEGORIES
    }


    fun getViewState() : LiveData<ViewState> {
        return  viewState
    }


    fun searchRecipesApi (_query : String, _pageNumber : Int) {
           pageNumber = _pageNumber
        if(!isPerformingQuery) {
               if(_pageNumber == 0) {
                   pageNumber = 1
               }
            query = _query
            isQueryExhausted = false
            executeSearch()
           }

    }

    fun searchNextPage() {
        if(!isQueryExhausted && !isPerformingQuery) {
            pageNumber ++
            executeSearch()
        }
    }



    fun executeSearch() {
        requestStartTime = System.currentTimeMillis()
        cancelRequest = false
        isPerformingQuery = true
        viewState.value = ViewState.RECIPES
        val repositorySource : LiveData<Resource<List<Recipe>>> = repository.searchRecipesApi(query, pageNumber)
        recipes.addSource(repositorySource, Observer {listResource ->
          if (!cancelRequest) {
              if (listResource != null) {
                  recipes.value = listResource
                  Log.d(TAG, "request time: + ${(System.currentTimeMillis() - requestStartTime) / 1000}")
                  if (listResource.status == Resource.Status.SUCCESS) {
                      isPerformingQuery = false
                      if (listResource.data != null) {
                          if (listResource.data.isEmpty()) {
                              Log.d(TAG, "On changed: query is exhausted...")
                              recipes.value = Resource<List<Recipe>>(Resource.Status.ERROR, listResource.data, QUERY_EXHAUSTED )
                              isPerformingQuery = true
                          }
                      }
                      recipes.removeSource(repositorySource)
                  } else if (listResource.status == Resource.Status.ERROR) {
                      isPerformingQuery = false
                      recipes.removeSource(repositorySource)
                  }

              } else {
                  recipes.removeSource(repositorySource)
              }
          } else {
              recipes.removeSource(repositorySource)
          }
        })
    }


    fun getRecipes() : LiveData<Resource<List<Recipe>>> {
        return recipes
    }

    fun setViewCategories() {
        viewState.value = ViewState.CATEGORIES
    }

    fun cancelSearchRequest() {
        if(isPerformingQuery) {
            Log.d(TAG, "cancel search request: canceling ")
            cancelRequest = true
            isPerformingQuery = false
            pageNumber = 1
        }
    }

}