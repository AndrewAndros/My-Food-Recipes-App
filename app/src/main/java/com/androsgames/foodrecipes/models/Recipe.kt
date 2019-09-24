package com.androsgames.foodrecipes.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "recipes")
class Recipe(

    @PrimaryKey
    @NonNull
    var recipe_id: String = "",

    @ColumnInfo
    var title: String = "",

    @ColumnInfo
    var publisher: String = "",

    @ColumnInfo
    var ingredients: Array<String?>? = null,

    @ColumnInfo
    var image_url: String = "",

    @ColumnInfo
    var source_url: String = "",


    @ColumnInfo
    var social_rank: Float = 0F,

    @ColumnInfo
    var timestamp: Int = 0,

    @ColumnInfo
    var bookmark: Int = 0
) : Parcelable