package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.usedbookmarket.databinding.ActivityReviewBinding
import com.example.usedbookmarket.databinding.ItemReviewTextBtnBinding
import com.example.usedbookmarket.model.ArticleForm
import com.example.usedbookmarket.model.Friend
import com.example.usedbookmarket.model.ReviewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var review: String
    private lateinit var list1: List<String>
    private lateinit var list2: List<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var desNickName: String
    private lateinit var detailReview: MutableList<String>

//    enum class State {
//        IDLE,
//        BAD,
//        GOOD
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.reviewRecyclerView
        detailReview= arrayListOf()

        // form 정보 가져오기
        val formModel: ArticleForm = intent.getParcelableExtra("formModel")!!

        // 좋았던 이유 리뷰 리스트
        list1 = listOf(
            "●  가격이 저렴해요.", "●  답장이 빨라요.", "●  매너가 좋아요.", "●  책 상태가 좋아요."
        )

        // 별로였던 이유 리뷰 리스트
        list2 = listOf(
            "●  시간약속을 안 지켜요.",
            "●  원하지 않는 가격을 계속 요구해요.",
            "●  거래 시간과 장소를 정한 후 거래 직전 취소했어요.",
            "●  약속 장소에 나타나지 않았어요.",
            "●  반말을 사용해요.",
            "●  불친절해요."
        )

        initView(formModel)

    }

    private fun initView(formModel: ArticleForm) {
        // 책 이미지 적용
        Glide.with(this)
            .load(formModel.coverSmallUrl)
            .apply(RequestOptions().circleCrop())
            .into(binding.reviewImage)

        // 책 제목 적용
        binding.reviewTitle.text = formModel.title

        // 자신과 상대방 이름 적용
        val uid = Firebase.auth.currentUser?.uid!!
        val destinationUid = intent.getStringExtra("destinationUid")
        val ref = FirebaseDatabase.getInstance().reference

        ref.child("users").child(uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                // 자신의 이름
                val userProfile = snapshot.getValue<Friend>()
                binding.reviewUidView.text = userProfile?.name + "님"
            }
        })

        ref.child("users").child("$destinationUid").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                // 상대방의 이름
                val desProfile = snapshot.getValue<Friend>()
                desNickName= desProfile?.name!!
                binding.reviewDestinationIdView.text = desNickName+ "님"
            }
        })
        // 리뷰 버튼 초기화
        val btn1 = binding.reviewBadBtn
        val btn2 = binding.reviewGoodBtn
        val btn3 = binding.reviewVeryGoodBtn

        btn1.setOnClickListener {
            btn1.alpha = 1f
            btn2.alpha = 0.5f
            btn3.alpha = 0.5f
            review = "bad"
            changeListAdapter(list2)

        }
        btn2.setOnClickListener {
            btn1.alpha = 0.5f
            btn2.alpha = 1f
            btn3.alpha = 0.5f
            review = "good"
            changeListAdapter(list1)
        }
        btn3.setOnClickListener {
            btn1.alpha = 0.5f
            btn2.alpha = 0.5f
            btn3.alpha = 1f
            review = "veryGood"
            changeListAdapter(list1)
        }

        // 완료 버튼
        binding.reviewCompleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setTitle("보내기")
                .setMessage("\"$desNickName\"님에게 후기를 보냅니다.")
                .setPositiveButton("입력완료",
                    DialogInterface.OnClickListener { dialog, id ->
                        if(detailReview.isEmpty()) {
                            // 아무런 상세 리뷰를 클릭하지 않았을 시
                            Toast.makeText(this, "리뷰를 클릭해주세요.", Toast.LENGTH_SHORT).show()
                            return@OnClickListener
                        }else{
                            // 외부 DB에 저장
                            val review= ReviewModel(destinationUid, review, detailReview)
                            FirebaseDatabase.getInstance()
                                .getReference("review_list/$uid").push()
                                .setValue(review)

                        Toast.makeText(this, "보내기 완료!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }

    }

    private fun changeListAdapter(list: List<String>) {
        // 상세 리뷰 적용
        val adapter = MyAdapter(list)
        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    inner class MyAdapter(private val itemList: List<String>) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemReviewTextBtnBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = itemList[position]
            holder.bind(item)
        }

        override fun getItemCount() = itemList.size

        inner class ViewHolder(private val binding: ItemReviewTextBtnBinding) :
            RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("NewApi")
            fun bind(item: String) {
                val btn = binding.itemViewBtn
                btn.text = item

                var isClicked = false // 클릭 여부를 저장할 변수

                binding.itemViewBtn.setOnClickListener {
                    isClicked = !isClicked // 클릭 여부를 반전시킴

                    if (isClicked) {
                        detailReview.add(item)
                        btn.setTextColor(getColor(R.color.gold)) // 빨간색으로 설정
                    } else {
                        detailReview.remove(item)
                        btn.setTextColor(getColor(R.color.white)) // 원래 색으로 설정
                    }
                }
            }
        }

    }
}
