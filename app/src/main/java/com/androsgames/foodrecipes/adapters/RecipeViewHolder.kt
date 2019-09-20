package com.androsgames.foodrecipes.adapters

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.androsgames.foodrecipes.R
import com.androsgames.foodrecipes.models.Recipe
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import kotlin.math.roundToInt

class RecipeViewHolder(itemView: View, private val onRecipeListener: OnRecipeListener, val requestManager : RequestManager,  private var preloadSizeProvider : ViewPreloadSizeProvider<String>) : RecyclerView.ViewHolder(itemView), View.OnClickListener   {


    val title : TextView = itemView.findViewById(R.id.recipe_title)
    val publisher : TextView = itemView.findViewById(R.id.recipe_publisher)
    val socialScore : TextView = itemView.findViewById(R.id.recipe_social_score)
    val image : AppCompatImageView = itemView.findViewById(R.id.recipe_image)




    fun onBind(recipe : Recipe) {


      requestManager
            .load(recipe.image_url)
            .into(image)

        title.text = recipe.title
        publisher.text = recipe.publisher
        socialScore.text = recipe.social_rank.roundToInt().toString()
        preloadSizeProvider.setView(image)
    }


    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v : View?) {
               onRecipeListener.onRecipeClick(adapterPosition)
    }



}