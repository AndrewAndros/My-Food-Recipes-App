package com.androsgames.foodrecipes.adapters

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.androsgames.foodrecipes.R
import com.androsgames.foodrecipes.models.Recipe
import com.bumptech.glide.RequestManager
import de.hdodenhof.circleimageview.CircleImageView

class CategoryViewHolder(itemView: View, private val listener : OnRecipeListener, val requestManager : RequestManager) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var categoryImage : CircleImageView = itemView.findViewById(R.id.category_image)
    var categoryTitle : TextView = itemView.findViewById(R.id.category_title)

    init {
        itemView.setOnClickListener(this)
    }

    fun onBind(recipe : Recipe) {



        val path : Uri = Uri.parse("android.resource://com.androsgames.foodrecipes/drawable/" + recipe.image_url )
           requestManager
            .load(path)
            .into(categoryImage)

        categoryTitle.text = recipe.title
    }

    override fun onClick(v: View?) {
        listener.onCategoryClick(categoryTitle.text.toString())
    }


}