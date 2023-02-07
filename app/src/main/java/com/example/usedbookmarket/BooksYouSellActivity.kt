package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.adapter.BooksYouSellListAdapter
import com.example.usedbookmarket.model.ArticleForm
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BooksYouSellActivity: AppCompatActivity() {
    private lateinit var articleDB: DatabaseReference
    private lateinit var adapter: BooksYouSellListAdapter
    private lateinit var recyclerView: RecyclerView
    private val articleFormList= mutableListOf<ArticleForm>()
    private var auth= Firebase.auth

    @SuppressLint("MissingInflatedId")
    private val listener= object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleForm: ArticleForm? = snapshot.getValue(ArticleForm::class.java)
            articleForm ?: return
            if(articleForm.uid == auth.currentUser?.uid){
                articleFormList.add(articleForm)
            }
            adapter.submitList(articleFormList)
            adapter.notifyDataSetChanged()
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) { }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books_you_sell)

        //TODO 해당 계정 내에 소지 중인 책 데이터 가져오기

        recyclerView = findViewById(R.id.books_you_sell_recyclerView)


        adapter= BooksYouSellListAdapter {  }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        articleDB= Firebase.database.reference.child("sell_list")

        articleDB.addChildEventListener(listener)

        adapter.submitList(articleFormList)



    }
}