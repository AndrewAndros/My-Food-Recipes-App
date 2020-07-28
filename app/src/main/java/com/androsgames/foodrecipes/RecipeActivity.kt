package com.androsgames.foodrecipes

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.AppCompatImageView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.androsgames.foodrecipes.databinding.ActivityRecipeBinding
import com.androsgames.foodrecipes.models.Recipe
import com.androsgames.foodrecipes.persistence.RecipeDao
import com.androsgames.foodrecipes.persistence.RecipeDatabase
import com.androsgames.foodrecipes.util.Resource
import com.androsgames.foodrecipes.viewmodels.RecipeViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class RecipeActivity : BaseActivity() {

    private val TAG = "RecipeActivity"
    lateinit var  mRecipeViewModel: RecipeViewModel
    private lateinit var recipe: Recipe
    private var recipeDao: RecipeDao = RecipeDatabase.getInstance(this)!!.recipeDAO()

    private var disposables: CompositeDisposable = CompositeDisposable()
    private val appExecuters: AppExecutors = AppExecutors.get()

    lateinit var bind: ActivityRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        bind=ActivityRecipeBinding.inflate(layoutInflater)
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)
        showProgressBar(true)
        // subscribeObservers()
        getIncomingIntent()

       bind.recipeSourceIntentBn.setOnClickListener {
            openSource()
        }

//        bookmarkBn.setOnClickListener {
//            addOrRemoveFromBookmarks()
//        }


        // Getting rid of excessive clicks
        bind.bookmarkBtn.clicks()
            .throttleFirst(1800, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Unit> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: Unit) {
                    addOrRemoveFromBookmarks()
                }

                override fun onError(e: Throwable) {

                }

            })


    }


    // If the recipe isn't in bookmarks mark it or visa versa (demark it)
    private fun addOrRemoveFromBookmarks() {
        if (recipe.bookmark == 1) {
            recipe.bookmark = 0
            bind.bookmarkBtn.setImageResource(R.drawable.ic_emptystar)
            Toast.makeText(this, "recipe removed from bookmarks", Toast.LENGTH_SHORT).show()
        } else {
            recipe.bookmark = 1
           bind.bookmarkBtn.setImageResource(R.drawable.ic_filledstar)
            Toast.makeText(this, "recipe added to bookmarks", Toast.LENGTH_SHORT).show()
        }
        appExecuters.diskIO().execute {

            recipeDao.updateRecipe(
                recipe.recipe_id,
                recipe.title,
                recipe.publisher,
                recipe.image_url,
                recipe.source_url,
                recipe.social_rank,
                recipe.bookmark
            )
        }
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("recipe")) {
            recipe = intent.getParcelableExtra("recipe")
            subscribeObservers(recipe.recipe_id)
        }
    }

    // Leads us to WebSite where the recipe was originally published
    private fun openSource() {
        val url = recipe.source_url
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun subscribeObservers(recipeId: String) {
        mRecipeViewModel.searchRecipeApi(recipeId)
            .observe(this, android.arch.lifecycle.Observer<Resource<Recipe>> { recipeResource ->
                if (recipeResource != null) {
                    when (recipeResource.status) {
                        Resource.Status.LOADING -> showProgressBar(true)
                        Resource.Status.ERROR -> {
                            Log.d(TAG, "OnChanged: Status : Error ")
                            Log.d(TAG, "OnChanged: ERROR MESSAGE: ${recipeResource.message}")
                            showParent()
                            showProgressBar(false)
                            setRecipeProperties(recipeResource.data)
                        }
                        Resource.Status.SUCCESS -> {
                            Log.d(TAG, "OnChanged: cache has been refreshed ")
                            Log.d(
                                TAG,
                                "OnChanged: status: SUCCESS, Recipe: ${recipeResource.data!!.title}"
                            )
                            showParent()
                            showProgressBar(false)
                            setRecipeProperties(recipeResource.data)
                        }
                    }
                }
            })


    }

    private fun setRecipeProperties(recipe: Recipe?) {
        if (recipe != null) {

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background)

            Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(recipe.image_url)
                .into(bind.recipeImage)

            bind.recipeTitle.text = recipe.title
            bind.recipePublisher.text = "Publisher: ${recipe.publisher}"
            bind.recipeSocialScore.text = recipe.social_rank.roundToInt().toString()
            if (recipe.bookmark == 1) {
                bind.bookmarkBtn.setImageResource(R.drawable.ic_filledstar)
            }
            if (recipe.ingredients != null) {
                setIngredientsWithRx(recipe.ingredients)
            } else {
                setIngredient(null, true)
            }
        }


    }


    private fun setIngredientsWithRx(ingredients: Array<String?>?) {
        bind.ingredientsContainer.removeAllViews()
        Observable.fromArray(*ingredients as Array<out String>)
            .filter {
                it.isNotBlank()
            }
            .distinct()
            .map { it -> "* $it" }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: String) {
                    setIngredient(t, false)
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "Error with setting ingredients using Rx: ${e.message}")
                    setIngredient(null, true)
                }
            })


    }

    private fun setIngredient(ingr: String?, error: Boolean) {
        val textView = TextView(this)

        if (error) {
            textView.text = "Error retrieving ingredients.\n Check network connection"
        } else {
            textView.text = ingr
        }
        textView.textSize = 15F
        textView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        bind.ingredientsContainer.addView(textView)

    }


    private fun showParent() {
        bind.parent.visibility = View.VISIBLE
    }


    private fun displayErrorScreen(errorMessage: String) {
      bind.recipeTitle.text = "error retrieving recipe"
       bind.recipeSocialScore.text = ""
        val textView = TextView(this)
        if (!errorMessage.equals("")) {
            textView.text = errorMessage
        } else {
            textView.text = "Error"
        }
        textView.textSize = 15F
        textView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
       bind.ingredientsContainer.addView(textView)

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)

        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(R.drawable.ic_launcher_background)
            .into(bind.recipeImage)

        showParent()
        showProgressBar(false)
        mRecipeViewModel.setRetrievedRecipe(true)

    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }


}
