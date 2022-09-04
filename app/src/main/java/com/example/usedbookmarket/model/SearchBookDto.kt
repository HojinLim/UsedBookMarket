package com.example.usedbookmarket.model

import com.example.usedbookmarket.model.Book
import com.google.gson.annotations.SerializedName

data class SearchBooksDto(
    @SerializedName("items") val books: List<Book>
)
