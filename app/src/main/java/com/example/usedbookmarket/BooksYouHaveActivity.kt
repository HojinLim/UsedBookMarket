package com.example.usedbookmarket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.adapter.BooksYouHaveListAdapter
import com.example.usedbookmarket.model.ArticleForm
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BooksYouHaveActivity: AppCompatActivity() {
    private lateinit var adapter: BooksYouHaveListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var booksYouHaveDB: DatabaseReference
    private val booksYouHaveList= mutableListOf<ArticleForm>()

    private val listener= object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleForm: ArticleForm? = snapshot.getValue(ArticleForm::class.java)
            articleForm ?: return
            booksYouHaveList.add(articleForm)
            adapter.submitList(booksYouHaveList)
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
        setContentView(R.layout.activity_books_you_trade)

        findViewById<FloatingActionButton>(R.id.books_you_have_floatBtn).setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

        //TODO 해당 계정 내에 소지 중인 책 데이터 가져오기
        booksYouHaveDB = Firebase.database.reference.child("books_you_have_list")



        recyclerView = findViewById(R.id.books_you_have_recyclerView)

        adapter = BooksYouHaveListAdapter(clickListener = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        booksYouHaveDB.addChildEventListener(listener)


    }
}