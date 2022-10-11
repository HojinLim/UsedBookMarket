package com.example.usedbookmarket.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ChatList(
    val userProfileUrl: String?,
    val userNickName: String?,
    val lastMsg: String?,
    val time: String?,
    val bookImgUrl: String?
): Parcelable
