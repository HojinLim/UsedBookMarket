package com.example.usedbookmarket.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ReviewModel(
    val desUid: String?= "",
    val review: String?= "",
    val aid: String?= "", // 책 form을 가지고 오기위한
    val detailReview: List<String>?= listOf()

)

