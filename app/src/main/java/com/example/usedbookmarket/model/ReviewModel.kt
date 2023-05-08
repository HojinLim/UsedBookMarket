package com.example.usedbookmarket.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ReviewModel(
    val desUid: String?= "",
    val review: String?= "",
    val detailReview: List<String>?= listOf()

)

