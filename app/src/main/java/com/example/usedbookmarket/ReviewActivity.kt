package com.example.usedbookmarket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.usedbookmarket.databinding.ActivityReviewBinding
import com.example.usedbookmarket.databinding.ItemReviewTextBtnBinding
import com.example.usedbookmarket.model.ArticleForm
import com.example.usedbookmarket.model.Friend
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // form 정보 가져오기
        val formModel: ArticleForm = intent.getParcelableExtra("formModel")!!

        initView(formModel)

    }

    private fun initView(formModel: ArticleForm) {
        // 책 이미지 적용
        Glide.with(this)
            .load(formModel.coverSmallUrl)
            .apply(RequestOptions().circleCrop())
            .into(binding.reviewImage)

        // 책 제목 적용
        binding.reviewTitle.text= formModel.title

        // 자신과 상대방 이름 적용
        val uid = Firebase.auth.currentUser?.uid!!
        val destinationUid = intent.getStringExtra("destinationUid")
        val ref= FirebaseDatabase.getInstance().reference

        ref.child("users").child(uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                // 자신의 이름
                val userProfile = snapshot.getValue<Friend>()
                binding.reviewUidView.text= userProfile?.name+"님"
            }
        })

        ref.child("users").child("$destinationUid").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                // 상대방의 이름
                val desProfile = snapshot.getValue<Friend>()
                binding.reviewDestinationIdView.text = desProfile?.name+"님"
            }
        })
        // 리뷰 버튼 초기화
        val btn1= binding.reviewBadBtn
        val btn2= binding.reviewGoodBtn
        val btn3= binding.reviewVeryGoodBtn

        btn1.setOnClickListener{
            btn1.alpha= 1f
            btn2.alpha= 0.5f
            btn3.alpha= 0.5f
        }
        btn2.setOnClickListener {
            btn1.alpha= 0.5f
            btn2.alpha= 1f
            btn3.alpha= 0.5f
        }
        btn3.setOnClickListener {
            btn1.alpha= 0.5f
            btn2.alpha= 0.5f
            btn3.alpha= 1f
        }
        // 상세 리뷰 적용
        val recyclerView = binding.reviewRecyclerView
        val adapter = MyAdapter(
            listOf
                ("●  가격이 저렴해요."
                , "●  답장이 빨라요."
                , "●  매너가 좋아요."
                , "●  책 상태가 좋아요."))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
    class MyAdapter(private val itemList: List<String>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemReviewTextBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = itemList[position]
            holder.bind(item)
        }

        override fun getItemCount() = itemList.size

        inner class ViewHolder(private val binding: ItemReviewTextBtnBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: String) {
                binding.itemViewBtn.text= item
            }
        }
    }

}
