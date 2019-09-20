package com.androsgames.foodrecipes.adapters

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androsgames.foodrecipes.R
import com.androsgames.foodrecipes.models.Recipe
import com.androsgames.foodrecipes.util.Constants
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt






class RecipeRecyclerAdapter (private var mOnRecipeListener : OnRecipeListener, var requestManager : RequestManager,  private var preloadSizeProvider : ViewPreloadSizeProvider<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ListPreloader.PreloadModelProvider<String> {
    override fun getPreloadItems(position: Int): MutableList<String> {
        val url : String = mRecipes?.get(position)!!.image_url
        if(TextUtils.isEmpty(url)) {
            return Collections.emptyList()
        }
        return Collections.singletonList(url)
    }

    override fun getPreloadRequestBuilder(item: String): RequestBuilder<*>? {
        return requestManager.load(item)
    }


    companion object {
        private const val RECIPE_TYPE = 1
        private const val LOADING_TYPE = 2
        private const val CATEGORY_TYPE = 3
        private const val EXHAUSTED_TYPE = 4
    }

     private var mRecipes: ArrayList<Recipe>? = ArrayList()



    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {

        var view : View? = null

        return when (i) {
            RECIPE_TYPE -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_recipe_list_item, viewGroup, false)
                RecipeViewHolder(view, mOnRecipeListener, requestManager, preloadSizeProvider)
            }
            LOADING_TYPE -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_loading_list_item, viewGroup, false)
                LoadingViewHolder(view)
            }
            CATEGORY_TYPE -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_category_list_item, viewGroup, false)
                CategoryViewHolder(view, mOnRecipeListener, requestManager)
            }
            EXHAUSTED_TYPE -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_search_exhausted, viewGroup, false)
                SearchExhaustedViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_recipe_list_item, viewGroup, false)
                RecipeViewHolder(view, mOnRecipeListener, requestManager, preloadSizeProvider)
            }
        }

    }

    override fun getItemCount(): Int {
        if (mRecipes != null) {
            return mRecipes!!.size
        }
        return 0
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val itemViewType : Int = getItemViewType(i)
        if (itemViewType == RECIPE_TYPE) {

        (viewHolder as RecipeViewHolder).onBind(mRecipes!!.get(i))

        } else if (itemViewType == CATEGORY_TYPE) {
            (viewHolder as CategoryViewHolder).onBind(mRecipes!!.get(i))

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mRecipes!![position].social_rank.roundToInt() == -1) {
            CATEGORY_TYPE
        } else if (mRecipes!![position].title == "LOADING...") {
            LOADING_TYPE
        } else if (mRecipes!![position].title == "EXHAUSTED...") {
            EXHAUSTED_TYPE
        }
        else {
            RECIPE_TYPE
        }
    }


    // display loading during search
    fun displayOnlyLoading() {
          clearRecipesList()
          val recipe : Recipe = Recipe()
          recipe.title = "LOADING..."
          mRecipes?.add(recipe)
          notifyDataSetChanged()
    }

    fun clearRecipesList() {
        if (mRecipes == null) {
            mRecipes = ArrayList()
        } else {
            mRecipes?.clear()
        }
        notifyDataSetChanged()
    }

    fun setQueryExhausted() {
        hideLoading()
        val exhaustedRecipe : Recipe = Recipe()
        exhaustedRecipe.title = "EXHAUSTED..."
        mRecipes!!.add(exhaustedRecipe)
        notifyDataSetChanged()
    }

     fun hideLoading() {
        if(isLoading()) {
                if (mRecipes?.get(0)?.title.equals("LOADING...")) {
                       mRecipes?.removeAt(0)
                }
                else if (mRecipes?.get(mRecipes!!.size - 1)!!.title.equals("LOADING...")) {
                mRecipes?.removeAt(mRecipes!!.size - 1)
                }
        }
         notifyDataSetChanged()
    }

    fun displaySearchCategories() {
        val categories = ArrayList<Recipe>()
        for (a in Constants.DEFAULT_SEARCH_CATEGORIES.indices) {
              val recipe = Recipe()
              recipe.title = Constants.DEFAULT_SEARCH_CATEGORIES[a]
              recipe.image_url = Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[a]
              recipe.social_rank = -1F
              categories.add(recipe)
        }
        mRecipes = categories
        notifyDataSetChanged()
    }

    // pagination loading
    fun displayLoading() {
        if (mRecipes == null) {
            mRecipes = ArrayList()
        }
        if (!isLoading()) {
            val recipe : Recipe = Recipe()
            recipe.title = "LOADING..."
            mRecipes?.add(recipe)
            notifyDataSetChanged()
        }
    }


    private fun isLoading() : Boolean {
        if(mRecipes != null && mRecipes!!.size > 0) {
            if (mRecipes!![mRecipes!!.size - 1].title == "LOADING...") {
                return true
            }
        }
        return false
    }

    fun getSelectedRecipe(position : Int) : Recipe? {
        if (mRecipes != null && mRecipes!!.size > 0) {
            return mRecipes!![position]
        }
        return null
    }

    fun setRecipes(recipes: List<Recipe>) {
        mRecipes = recipes as ArrayList<Recipe>
        notifyDataSetChanged()
    }
}