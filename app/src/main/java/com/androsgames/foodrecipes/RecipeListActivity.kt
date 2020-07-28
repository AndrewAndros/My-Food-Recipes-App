package com.androsgames.foodrecipes

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
import com.androsgames.foodrecipes.adapters.OnRecipeListener
import com.androsgames.foodrecipes.adapters.RecipeRecyclerAdapter
import com.androsgames.foodrecipes.databinding.ActivityRecipeListBinding
import com.androsgames.foodrecipes.models.Recipe
import com.androsgames.foodrecipes.util.Resource
import com.androsgames.foodrecipes.util.VerticalSpacingItemDecorator
import com.androsgames.foodrecipes.viewmodels.RecipeListViewModel
import com.androsgames.foodrecipes.viewmodels.RecipeListViewModel.Companion.QUERY_EXHAUSTED
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider

class RecipeListActivity : BaseActivity(), OnRecipeListener {


    val TAG : String = "RecipeListActivity"

    lateinit var bindin: ActivityRecipeListBinding
    private lateinit var mRecipeListViewModel : RecipeListViewModel

    private lateinit var mRecyclerAdapter : RecipeRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        bindin=ActivityRecipeListBinding.inflate(layoutInflater)

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel::class.java)
        initRecyclerView()
        initSearchView()
        subscribeObservers()
//        testRetrofitRequest()

//        findViewById<Button>(R.id.test).setOnClickListener {
//            testRetrofitRequest()
//        }

        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar)
    }

    private fun displaySearchCategories() {
        mRecyclerAdapter.displaySearchCategories()
    }


    private fun initSearchView() {

        bindin.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(s: String): Boolean {

                 searchRecipesApi(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
               return false
            }

        })
    }


    private fun initRecyclerView() {
        val viewPreloader : ViewPreloadSizeProvider<String> = ViewPreloadSizeProvider()
        mRecyclerAdapter = RecipeRecyclerAdapter(this,  initGlide(), viewPreloader)
        val itemDecorator = VerticalSpacingItemDecorator(30)
        bindin.recipeList.addItemDecoration(itemDecorator)
        bindin.recipeList.layoutManager = LinearLayoutManager(this)

        val preloader : RecyclerViewPreloader<String> = RecyclerViewPreloader<String>(Glide.with(this), mRecyclerAdapter, viewPreloader, 30 )


            bindin.recipeList.addOnScrollListener(preloader)

        bindin.recipeList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(!bindin.recipeList.canScrollVertically(1) && mRecipeListViewModel.getViewState().value == RecipeListViewModel.ViewState.RECIPES) {
                    mRecipeListViewModel.searchNextPage()
                }
            }
        })


        bindin.recipeList.adapter = mRecyclerAdapter


    }

    private fun subscribeObservers() {
                  mRecipeListViewModel.getRecipes().observe(this,
                      Observer<Resource<List<Recipe>>?> {listResource ->
                          if(listResource != null){
                              Log.d(TAG, "onChanged: status: ")

                              if(listResource.data != null){
                                         when(listResource.status) {
                                                    Resource.Status.LOADING -> {
                                                       if(mRecipeListViewModel.pageNumber > 1) {
                                                           mRecyclerAdapter.displayLoading()
                                                       } else {
                                                           mRecyclerAdapter.displayOnlyLoading()
                                                       }
                                                    }
                                                        Resource.Status.ERROR -> {
                                                            Log.e(TAG, "onChanged: cannot refresh cache.")
                                                            Log.e(TAG, "onChanged: ERROR message: " + listResource.message )
                                                            Log.e(TAG, "onChanged: status: ERROR, #Recipes: " + listResource.data.size)
                                                            mRecyclerAdapter.hideLoading()
                                                            mRecyclerAdapter.setRecipes(listResource.data)
                                                            Toast.makeText(this, listResource.message, Toast.LENGTH_SHORT).show()

                                                            if(listResource.message.equals(QUERY_EXHAUSTED)){
                                                                mRecyclerAdapter.setQueryExhausted()
                                                            }
                                                        }
                                                         Resource.Status.SUCCESS -> {
                                                             Log.d(TAG, "onChanged: cache has been refreshed.")
                                                             Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size)
                                                             mRecyclerAdapter.hideLoading();
                                                             mRecyclerAdapter.setRecipes(listResource.data)

                                                         }
                                         }
                              }
                          }
                      })


        mRecipeListViewModel.getViewState().observe(this, Observer {
                      if(it != null) {

                          when (it) {
                              RecipeListViewModel.ViewState.RECIPES ->  {
                             }
                              else -> {
                                  displaySearchCategories()
                              }
                          }
                      }
                  })
    }

    fun searchRecipesApi (query : String) {
        bindin.recipeList.smoothScrollToPosition(0)
        mRecipeListViewModel.searchRecipesApi(query, 1)
       bindin.searchView.clearFocus()
    }




    override fun onRecipeClick(position: Int) {
        val intent  = Intent(this, RecipeActivity::class.java)
        intent.putExtra("recipe", mRecyclerAdapter.getSelectedRecipe(position))
        startActivity(intent)
    }

    override fun onCategoryClick(category: String) {
             searchRecipesApi(category)
    }


    private fun initGlide() : RequestManager {
        val options = RequestOptions()
            .placeholder(R.drawable.white_background)
            .error(R.drawable.white_background)

       return  Glide.with(this)
            .setDefaultRequestOptions(options)
    }


    override fun onBackPressed() {
        if(mRecipeListViewModel.getViewState().value == RecipeListViewModel.ViewState.CATEGORIES) {
           super.onBackPressed()
        } else {
            mRecipeListViewModel.cancelSearchRequest()
            mRecipeListViewModel.setViewCategories()
        }
    }



}


