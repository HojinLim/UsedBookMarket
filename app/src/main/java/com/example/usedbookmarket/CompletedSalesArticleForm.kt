package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.usedbookmarket.model.ArticleForm
import com.google.firebase.auth.FirebaseAuth

class CompletedSalesArticleForm : AppCompatActivity() {
    var isLiked= false
    val auth= FirebaseAuth.getInstance()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_sales_article_form)

        initView()
        findViewById<AppCompatButton>(R.id.books_you_have_backButton).setOnClickListener {
            onBackPressed()
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
    @SuppressLint("ResourceAsColor")
    private fun initView(){
        val formModel = intent.getParcelableExtra<ArticleForm>("formModel")
        formModel ?: return

        // 자신이 작성한 글이 아닐 시 수정 불가
        if(formModel.uid != auth.currentUser?.uid){
            findViewById<Button>(R.id.article_form_complete_btn).isVisible= false
        }

        val coverImageView = findViewById<ImageView>(R.id.c_sales_article_form_coverImg)
        findViewById<TextView>(R.id.c_sales_article_form_detail_title).text = formModel?.title.orEmpty()
        Glide
            .with(coverImageView.context)
            .load(formModel?.coverSmallUrl.orEmpty())
            .into(coverImageView)
        findViewById<TextView>(R.id.article_form_discount).text = formModel?.priceSales.orEmpty()
        findViewById<TextView>(R.id.c_sales_article_form_title).text= formModel?.formTitle.orEmpty()
        findViewById<TextView>(R.id.c_sales_article_form_description).text= formModel?.formDescription.orEmpty()
        findViewById<TextView>(R.id.c_sales_article_form_wish_price).text= formModel?.wishPrice.orEmpty()

        findViewById<AppCompatButton>(R.id.c_sales_article_like).let { it ->
            it.setOnClickListener{  // 하트 색 제어
                isLiked = if(!isLiked){
                    it.background.setTint(Color.RED)
                    true
                }else{
                    it.background.setTint(Color.WHITE)
                    false
                }
            }
        }
        findViewById<AppCompatButton>(R.id.c_sales_article_chat_btn).setOnClickListener {
            val intent= Intent(this, ChatActivity::class.java)
            intent.putExtra("msgSend", auth.currentUser?.email)
            startActivity(intent)
        }
    }
}
