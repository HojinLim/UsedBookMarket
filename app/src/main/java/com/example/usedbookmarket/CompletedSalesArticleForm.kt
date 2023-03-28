package com.example.usedbookmarket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.usedbookmarket.databinding.ActivityCompletedSalesArticleFormBinding
import com.example.usedbookmarket.model.ArticleForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


//Toast.makeText(this, "hello there", Toast.LENGTH_SHORT).show()
class CompletedSalesArticleForm : AppCompatActivity() {
    var isLiked = false
    var isBusy = false
    val auth = FirebaseAuth.getInstance()
    private var reference = Firebase.database
    private lateinit var binding: ActivityCompletedSalesArticleFormBinding
    private lateinit var formModel: ArticleForm
    private lateinit var heart: AppCompatButton
    private lateinit var uid: String
    private lateinit var aid: String

    lateinit var sliderViewPager: ViewPager2
    lateinit var layoutIndicator: LinearLayout
    private val images = arrayOf(
        "https://cdn.pixabay.com/photo/2019/12/26/10/44/horse-4720178_1280.jpg",
        "https://cdn.pixabay.com/photo/2020/11/04/15/29/coffee-beans-5712780_1280.jpg",
        "https://cdn.pixabay.com/photo/2020/03/08/21/41/landscape-4913841_1280.jpg",
        "https://cdn.pixabay.com/photo/2020/09/02/18/03/girl-5539094_1280.jpg",
        "https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg"
    )

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCompletedSalesArticleFormBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sliderViewPager = binding.sliderViewPager
        layoutIndicator = binding.layoutIndicators

        sliderViewPager.offscreenPageLimit = 1
        sliderViewPager.adapter = ImageSliderAdapter(this, images)
        sliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        setupIndicators(images.size)



        // 뒤로가기 버튼
        findViewById<AppCompatButton>(R.id.books_you_have_backButton).setOnClickListener {
            onBackPressed()
        }

        formModel = intent.getParcelableExtra<ArticleForm>("formModel")!!
        formModel ?: return

        heart = binding.cSalesArticleLike
        uid = auth.currentUser?.uid!!   // 현재 유저 uid
        aid = formModel.aid!!   // 현재 글의 aid

        initView()  // 현재 글 소유권에 따른 뷰 배치


        // 채팅 버튼 누를 시
        findViewById<AppCompatButton>(R.id.c_sales_article_chat_btn).setOnClickListener {
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("destinationUid", formModel.uid)
            this.startActivity(intent)
        }

        // 자신의 글일 시의 수정 버튼
        findViewById<Button>(R.id.article_form_edit_btn).setOnClickListener {
            val intent = Intent(this, SalesArticleFormActivity2::class.java)
            intent.putExtra("formModel", formModel)
            startActivity(intent)
        }
    }

    private fun setupIndicators(count: Int) {
        val indicators: Array<ImageView?> = arrayOfNulls<ImageView>(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 8, 16, 8)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.bg_indicator_inactive
                )
            )
            indicators[i]?.layoutParams = params
            layoutIndicator.addView(indicators[i])
        }
        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = layoutIndicator.childCount
        for (i in 0 until childCount) {
            val imageView: ImageView = layoutIndicator.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                    )
                )
            }
        }
    }
    // 내부 이미지 슬라이더 어댑터
    inner class ImageSliderAdapter(context: Context, sliderImage: Array<String>) :
        RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {
        private val context: Context
        private val sliderImage: Array<String>

        init {
            this.context = context
            this.sliderImage = sliderImage
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_slider, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindSliderImage(sliderImage[position])
        }

        override fun getItemCount(): Int {
            return sliderImage.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val mImageView: ImageView

            init {
                mImageView = itemView.findViewById(R.id.imageSlider)
            }

            fun bindSliderImage(imageURL: String?) {
                Glide.with(context)
                    .load(imageURL)
                    .into(mImageView)
            }
        }
    }

    private fun initView() {
        // 공통 부분
        val coverImageView = findViewById<ImageView>(R.id.c_sales_article_form_coverImg)
        findViewById<TextView>(R.id.c_sales_article_form_detail_title).text =
            formModel?.title.orEmpty()
        Glide
            .with(coverImageView.context)
            .load(formModel?.coverSmallUrl.orEmpty())
            .into(coverImageView)

        // 이미지 확대
        coverImageView.setOnClickListener {
            isBusy= true
            val intent= Intent(this, ZoomImageActivity::class.java)
            intent.putExtra("formImage", formModel.coverSmallUrl)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.article_form_discount).text = formModel?.priceSales.orEmpty()
        findViewById<TextView>(R.id.c_sales_article_form_title).text =
            formModel?.formTitle.orEmpty()
        findViewById<TextView>(R.id.c_sales_article_form_description).text =
            formModel?.formDescription.orEmpty()
        findViewById<TextView>(R.id.c_sales_article_form_wish_price).text =
            formModel?.wishPrice.orEmpty()

        // 자신이 작성한 글일 시 수정, 채팅, 관심 버튼 사라짐
        // 상대방 글
        if (formModel.uid != uid) {
            findViewById<Button>(R.id.article_form_edit_btn).isVisible = false
            findViewById<AppCompatButton>(R.id.c_sales_article_chat_btn).isVisible = true
            findViewById<AppCompatButton>(R.id.c_sales_article_like).isVisible = true


            reference.getReference("like_list/$uid/$aid/liked")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val value = dataSnapshot.getValue(String::class.java)
                        if(value== "true"){
                            Toast.makeText(this@CompletedSalesArticleForm, "hello there", Toast.LENGTH_SHORT).show()
                            isLiked= true

                        }else if(value =="false"){

                            isLiked= false
                        }


                        if(isLiked) {
                            heart.background.setTint(resources.getColor(R.color.red))

                        } else {    // 좋아요 값이 false이거나 좋아요 리스트 저장 안했을 시

                            heart.background.setTint(resources.getColor(R.color.white))
                        }

                    }



                    override fun onCancelled(databaseError: DatabaseError) {
                        //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                    }
                })


            binding.cSalesArticleLike.let { it ->
                it.setOnClickListener{  // 하트 색 제어
                    isLiked = if(!isLiked){
                        it.background.setTint(resources.getColor(R.color.red))
                        true
                    }else{
                        it.background.setTint(resources.getColor(R.color.white))
                        false
                    }
                }
            }
        } else {
            // 자신의 글
            findViewById<Button>(R.id.article_form_edit_btn).isVisible = true
            findViewById<AppCompatButton>(R.id.c_sales_article_chat_btn).isVisible = false
            findViewById<AppCompatButton>(R.id.c_sales_article_like).isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        isBusy= false
    }
    override fun onPause() {  // 이 화면이 사라지면 발동
        super.onPause()

        if(isBusy) return

        FirebaseDatabase.getInstance().reference.child("like_list/$uid")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) { }
                override fun onDataChange(snapshot: DataSnapshot) {

                        var formItem: ArticleForm?
                        for (data in snapshot.children) { // snapshot 자식들 사용 가능
                            val item = data.getValue<ArticleForm>()
                            if (item?.aid.equals(aid)) { // 내가 이미 추가한 값이면
                                Toast.makeText(this@CompletedSalesArticleForm, "추가된값이라 나갈겡~", Toast.LENGTH_SHORT).show()
                                this@CompletedSalesArticleForm.finish()
                            }else{
                                continue
                            }
                        }   // formItem -> 현 유저가 만들었던 좋아요 form
                    val changedForm = formModel.copy(liked = "true")

                    if(isLiked){
                        reference.getReference("like_list/$uid/$aid")
                            .setValue(changedForm)
                        Toast.makeText(this@CompletedSalesArticleForm, "true로 추가했엉 ..", Toast.LENGTH_SHORT).show()
                        //reference.getReference("sell_list/$uid/$aid")

                    }else{
                        reference.getReference("like_list/$uid/$aid")
                            .removeValue()
                    }

                }
            })
        this.finish()

    }
}










