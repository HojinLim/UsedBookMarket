package com.example.usedbookmarket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleForm(
    val uid: String?,
    val id: String?,
    val title: String?,
    val description: String?,
    val priceSales: String?,
    val coverSmallUrl: String?,
    val formTitle: String?,
    val formDescription: String?,
    val wishPrice: String?
): Parcelable
{
    constructor(): this("","","","",
    "","","","","")
}
/*
@SerializedName("isbn") val id: String,
       @SerializedName("title") val title: String,
       @SerializedName("description") val description: String,
       @SerializedName("discount") val priceSales: String?,
       @SerializedName("image") val coverSmallUrl: String,
       @SerializedName("link") val mobileLink: String?
 */
