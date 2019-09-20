package com.androsgames.foodrecipes.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull

@Entity(tableName = "recipes")
class Recipe()  : Parcelable  {

    @PrimaryKey
    @NonNull
    lateinit var recipe_id : String

    @ColumnInfo
    lateinit var title : String

    @ColumnInfo
    lateinit var publisher : String

    @ColumnInfo
    var ingredients : Array<String?>? = null

    @ColumnInfo
    lateinit var image_url : String

    @ColumnInfo
    lateinit var source_url : String


    @ColumnInfo
    var social_rank : Float = 0F

    @ColumnInfo
    var timestamp : Int = 0

    @ColumnInfo
    var bookmark : Int = 0


   protected constructor(parcel: Parcel) : this() {
        title = parcel.readString()
        publisher = parcel.readString()
        ingredients = parcel.createStringArray()
        recipe_id = parcel.readString()
        image_url = parcel.readString()
        source_url = parcel.readString()
        social_rank = parcel.readFloat()
        timestamp = parcel.readInt()
        bookmark = parcel.readInt()
    }





//    override fun toString(): String {
//        return "Recipe { title: $title " +
//                ", publisher: $publisher" +
//                ", ingredients: ${Arrays.toString(ingredients)}" +
//                ", recipe_id: $recipe_id" +
//                ", image_url: $image_url" +
//                "social_rank: $social_rank" +
//                "timestamp: $timestamp" +
//                '}'
//    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(publisher)
        parcel.writeStringArray(ingredients)
        parcel.writeString(recipe_id)
        parcel.writeString(image_url)
        parcel.writeString(source_url)
        parcel.writeFloat(social_rank)
        parcel.writeInt(timestamp)
        parcel.writeInt(bookmark)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }


}