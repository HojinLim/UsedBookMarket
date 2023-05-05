package com.example.usedbookmarket.model

class ChatModel(
    val users: HashMap<String, Boolean> = HashMap(),
    val comments: HashMap<String, Comment> = HashMap(),
    var aid: String?){
    constructor() : this(HashMap(),HashMap(),"")

    class Comment(val uid: String? = null, val message: String? = null, val time: String? = null)
}
