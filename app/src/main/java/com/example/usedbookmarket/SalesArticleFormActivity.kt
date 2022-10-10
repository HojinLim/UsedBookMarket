package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.usedbookmarket.model.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SalesArticleFormActivity:AppCompatActivity() {
    val database = Firebase.database.reference
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitiy_sales_article_form)

        val bookModel = intent.getParcelableExtra<Book>("bookModel")
        val coverImageView= findViewById<ImageView>(R.id.article_form_coverImg)





        findViewById<TextView>(R.id.article_form_detail_title).text= bookModel?.title.orEmpty()
        Glide
            .with(coverImageView.context)
            .load(bookModel?.coverSmallUrl.orEmpty())
            .into(coverImageView)
        findViewById<TextView>(R.id.article_form_discount).text= bookModel?.priceSales.orEmpty()

        findViewById<AppCompatButton>(R.id.article_form_complete_btn).setOnClickListener {
            val myRef = database.child(auth.currentUser.toString()).child("from")
            myRef.setValue("John")
        }
    }
}