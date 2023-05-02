package com.example.usedbookmarket.model

import android.os.Parcel
import android.os.Parcelable


data class ArticleForm(
    val aid: String?,
    val uid: String?,
    val id: String?,
    val title: String?,
    val description: String?,
    val priceSales: String?,
    val coverSmallUrl: String?,
    val formTitle: String?,
    val formDescription: String?,
    val wishPrice: String?,
    val createdAt: String?,
    val email: String?,
    val status: String?, //sale, reserve, sold
    var likeCount: Int

): Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    constructor() : this(
        "", "", "", "",
        "", "", "", "", "", "", "", "", "sale", 0
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(aid)
        parcel.writeString(uid)
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(priceSales)
        parcel.writeString(coverSmallUrl)
        parcel.writeString(formTitle)
        parcel.writeString(formDescription)
        parcel.writeString(wishPrice)
        parcel.writeString(createdAt)
        parcel.writeString(email)
        parcel.writeString(status)
        parcel.writeInt(likeCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArticleForm> {
        override fun createFromParcel(parcel: Parcel): ArticleForm {
            return ArticleForm(parcel)
        }

        override fun newArray(size: Int): Array<ArticleForm?> {
            return arrayOfNulls(size)
        }
    }


}





