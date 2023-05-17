package com.example.usedbookmarket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.usedbookmarket.adapter.CompleteReviewAdapter
import com.example.usedbookmarket.databinding.ItemReviewCompletedFormBinding
import com.example.usedbookmarket.model.ArticleForm
import com.example.usedbookmarket.model.Friend
import com.example.usedbookmarket.model.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ReviewYouGotActivity: AppCompatActivity() {
    private val fireDatabase = FirebaseDatabase.getInstance().reference

    private lateinit var adapter: ReviewActivity.MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewDB: DatabaseReference
    private val reviewModelList= mutableListOf<ReviewModel>()
    private val formModelList= mutableListOf<ArticleForm>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books_you_trade)


        val uid= FirebaseAuth.getInstance().currentUser?.uid
//        reviewDB = Firebase.database.reference.child("review_list/$uid")
        findViewById<AppCompatButton>(R.id.books_you_have_backButton).setOnClickListener {
            onBackPressed()
        }

        fireDatabase
            .child("review_list/$uid").addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(item in snapshot.children){
                        val rm= item.getValue<ReviewModel>()!!

                        reviewModelList.add(rm)

                        val aid= rm.aid
                        fireDatabase
                            .child("sell_list/$aid").addValueEventListener(object: ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val form=snapshot.getValue(ArticleForm::class.java)!!
                                    formModelList.add(form)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                        recyclerView.adapter?.notifyDataSetChanged()
//                        Log.d("TEST",rm.toString())
//                        Toast.makeText(this@ReviewYouGotActivity,rm.toString() ,Toast.LENGTH_SHORT).show()
                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        recyclerView = findViewById(R.id.books_you_have_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(reviewModelList)



    }
    inner class MyAdapter(private val itemList: MutableList<ReviewModel>) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        private val fireDatabase = FirebaseDatabase.getInstance().reference
        private var form: ArticleForm? = null
        private var friend: Friend? = null


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemReviewCompletedFormBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = itemList[position]
             //form= formModelList[0]

            holder.bind(item)
             //holder.bind2(form!!)
        }

        override fun getItemCount() = itemList.size

        inner class ViewHolder(private val binding: ItemReviewCompletedFormBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: ReviewModel) {

                // 상대방 닉네임 추출
                fireDatabase.child("users").child(item.desUid.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            friend = snapshot.getValue<Friend>()
                            binding.cReviewDesId.text =
                                friend?.name
                        }
                    })


                when(item.review){
                    "bad"-> binding.cReviewFace.setImageResource(R.drawable.kiss)
                    "good"-> binding.cReviewFace.setImageResource(R.drawable.smiling)
                    "veryGood"-> binding.cReviewFace.setImageResource(R.drawable.smile)
                }

                val recyclerView= binding.cReviewRecyclerView
                recyclerView.layoutManager = LinearLayoutManager(this@ReviewYouGotActivity)
                recyclerView.adapter = CompleteReviewAdapter(item.detailReview!!)
            }
            fun bind2(form: ArticleForm){
                // 책이름
                binding.cReviewBookName.text= form.title
                // 책 이미지 적용
                Glide.with(itemView.context).load(form.coverSmallUrl)
                    .apply(RequestOptions().centerCrop())
                    .into(binding.cReviewImg)

                binding.cReviewImg.setOnClickListener {// 이미지 확대
                    val intent= Intent(this@ReviewYouGotActivity, ZoomImageActivity::class.java)
                    intent.putExtra("formImage", form.coverSmallUrl)
                    startActivity(intent)
                }
            }
        }
    }


    }
