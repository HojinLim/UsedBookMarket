package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.usedbookmarket.model.Book

class SalesArticleFormActivity:AppCompatActivity() {
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

    }
}