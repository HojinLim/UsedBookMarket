package com.example.usedBookMarket.model

import com.example.usedbookmarket.model.Book
import com.google.gson.annotations.SerializedName

data class BestSellerDto(
    @SerializedName("title") val title: String,
    @SerializedName("item") val books: List<Book>
)
