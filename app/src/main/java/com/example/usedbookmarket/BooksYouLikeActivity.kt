package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.usedbookmarket.model.ArticleForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.*

class BooksYouLikeActivity: AppCompatActivity() {
    private lateinit var adapter: RecyclerViewAdapter

    private lateinit var recyclerView: RecyclerView
    private val likeList = ArrayList<ArticleForm>()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books_you_like)



        //TODO 해당 계정 내에 소지 중인 책 데이터 가져오기

        recyclerView = findViewById(R.id.books_you_like_recyclerView)

        adapter = RecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {
        private var isLiked= false


        private lateinit var uid: String
        private lateinit var email: String
        val auth = FirebaseAuth.getInstance()

        init{
            uid = auth.currentUser?.uid!!   // 현재 유저 uid
            email = auth.currentUser?.email!! // 현재 유저 email


            FirebaseDatabase.getInstance().reference.child("like_list/$uid").addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    likeList.clear()
                    for(data in snapshot.children){ // snapshot 자식들 사용 가능
                        val item = data.getValue<ArticleForm>()

                        likeList.add(item!!)
                    }
                    notifyDataSetChanged()


                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            return CustomViewHolder(LayoutInflater.from(this@BooksYouLikeActivity).inflate(R.layout.item_books_you_like, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.item_books_you_like_coverImageView)
            val articleTitle : TextView = itemView.findViewById(R.id.item_books_you_like_articleTitleTextView)
            val bookPrice : TextView = itemView.findViewById(R.id.item_article_priceTextView)
            val isLiked : AppCompatButton = itemView.findViewById(R.id.books_you_like_interest_btn)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            Glide.with(holder.itemView.context).load(likeList[position].coverSmallUrl)
                .apply(RequestOptions().centerCrop())
                .into(holder.imageView)

            holder.imageView.setOnClickListener {// 이미지 확대
                    val intent= Intent(this@BooksYouLikeActivity, ZoomImageActivity::class.java)
                    intent.putExtra("formImage", likeList[position].coverSmallUrl)
                    startActivity(intent)
            }

            holder.articleTitle.text= likeList[position].formTitle
            holder.bookPrice.text= likeList[position].wishPrice
            if(likeList[position].liked== "true"){
                holder.isLiked.background.setTint(resources.getColor(R.color.red))
            }else{
                holder.isLiked.background.setTint(resources.getColor(R.color.white))
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(this@BooksYouLikeActivity, CompletedSalesArticleForm::class.java)
                val likeForm: ArticleForm= likeList[position]
                intent.putExtra("formModel", likeForm)
                startActivity(intent)
            }


        }
        override fun getItemCount(): Int {
            return likeList.size
        }
    }
}