package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.adapter.BooksYouSellListAdapter
import com.example.usedbookmarket.model.Book

class BooksYouSellActivity: AppCompatActivity() {
    private lateinit var adapter: BooksYouSellListAdapter
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books_you_sell)

        //TODO 해당 계정 내에 소지 중인 책 데이터 가져오기

        recyclerView = findViewById(R.id.books_you_sell_recyclerView)

        adapter = BooksYouSellListAdapter(clickListener = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.submitList(listOf(
            Book("9788993827446","DDD Start!"
                ,"DDD의 핵심 개념을 배우고","3000","https://shopping-phinf.pstatic.net/main_3245626/32456266806.20220527031023.jpg","")
            , Book("9791162245385","도메인 주도 개발 시작하기"
                ,"가장 쉽게 배우는 도메인 주도 설계 입문서!","25200","https://shopping-phinf.pstatic.net/main_3243631/32436316743.20220527044029.jpg","")
        ))


    }
}