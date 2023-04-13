package com.example.usedbookmarket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val liked: String?,
    val createdAt: String?,
    val email: String?,
    val status: String? //sale, reserve, sold
): Parcelable
{
    constructor(): this("","","","",
    "","","","","","","", "","", "sale")
}

