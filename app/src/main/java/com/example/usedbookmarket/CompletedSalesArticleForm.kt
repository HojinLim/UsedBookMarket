package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


//Toast.makeText(this, "hello there", Toast.LENGTH_SHORT).show()
class CompletedSalesArticleForm : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    private lateinit var images: ArrayList<String?>
    var isLiked = false
    private var isBusy = false
    val auth = FirebaseAuth.getInstance()
    private var reference = Firebase.database
    private lateinit var binding: ActivityCompletedSalesArticleFormBinding
    private lateinit var formModel: ArticleForm
    private lateinit var heart: AppCompatButton
    private lateinit var uid: String
    private lateinit var aid: String
    private lateinit var userIdSt: String
    private lateinit var email: String
    private lateinit var initRef: ValueEventListener


    lateinit var sliderViewPager: ViewPager2
    lateinit var layoutIndicator: LinearLayout

    override fun onBackPressed() {
        super.onBackPressed()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCompletedSalesArticleFormBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()  // 현재 글 소유권에 따른 뷰 배치
        bindStatus(binding.statusImage,"load")

        // 책 상태툴바
        setSpinner()
        val spinner: Spinner = findViewById(R.id.book_status_spinner)
        spinner.onItemSelectedListener = this


        // 이미지 폴더 경로 참조
        val listRef = FirebaseStorage.getInstance().reference
            .child("userImages/formPhotos/$userIdSt/$aid")

        // listAll(): 폴더 내의 모든 이미지를 가져오는 함수

        listRef.listAll()
            .addOnSuccessListener { list ->
                
                images = arrayListOf("")


                for ((i, item) in list.items.withIndex()) {

                    // reference의 item(이미지) url 받아오기
                    item.downloadUrl.addOnSuccessListener {
                        images[i] = (it.toString())


                    }
                        .addOnFailureListener {
                            Toast.makeText(this, "hello there", Toast.LENGTH_SHORT).show()
                        }
                }

            }


        Handler(Looper.getMainLooper()).postDelayed({
            //실행할 코드
            sliderViewPager = binding.sliderViewPager
            layoutIndicator = binding.layoutIndicators

            sliderViewPager.offscreenPageLimit = 1
            sliderViewPager.adapter = ImageSliderAdapter(this, images)
            sliderViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                }
            })
            setupIndicators(images.size)
            bindStatus(binding.statusImage,"stop")
        }, 2000)

        // 뒤로가기 버튼
        findViewById<AppCompatButton>(R.id.books_you_have_backButton).setOnClickListener {
            onBackPressed()
        }

        // 채팅 버튼 누를 시
        findViewById<AppCompatButton>(R.id.c_sales_article_chat_btn).setOnClickListener {
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("destinationUid", formModel.uid)
            intent.putExtra("formModel",formModel)
            this.startActivity(intent)
        }
        
        // 자신의 글일 시의 수정 버튼
        findViewById<Button>(R.id.article_form_edit_btn).setOnClickListener {
            val intent = Intent(this, SalesArticleFormActivity::class.java)
            intent.putExtra("formModel", formModel)
            intent.putExtra("flag", "B")
            intent.putExtra("photo", images)
            startActivity(intent)
        }
    }
    private fun setSpinner() {
        val spinner: Spinner = binding.bookStatusSpinner
    // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.book_status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        when(formModel.status){
            "sale"-> spinner.setSelection(0)
            "reserve"-> spinner.setSelection(1)
            "sold" -> spinner.setSelection(2)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {

        when(parent.getItemAtPosition(pos).toString()){
            "판매중"-> initBookStatus("sale")
            "예약중"-> initBookStatus("reserve")
            "판매완료"-> initBookStatus("sold")

        }
    }
    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    private fun bindStatus(
        statusImageView: ImageView, status: String
    ) {
        when(status){
            "load"->{
                statusImageView.visibility = View.VISIBLE
                statusImageView.setImageResource(R.drawable.loading_animation)
            }
            "stop"->{
                statusImageView.visibility = View.GONE
            }
        }
    }

        private fun initView() {
            formModel = intent.getParcelableExtra("formModel")!!
            userIdSt = formModel.email!!
            heart = binding.cSalesArticleLike

            aid = formModel.aid!!
            email= auth.currentUser?.email!!

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
                isBusy = true
                val intent = Intent(this, ZoomImageActivity::class.java)
                intent.putExtra("formImage", formModel.coverSmallUrl)
                startActivity(intent)
            }

            // 0 원이라 나오면 ==> "정보 없음"
            if(formModel.priceSales == "0") findViewById<TextView>(R.id.article_form_discount).text= "정보없음"


            findViewById<TextView>(R.id.c_sales_article_form_title).text =
                formModel.formTitle.orEmpty()
            findViewById<TextView>(R.id.c_sales_article_form_description).text =
                formModel.formDescription.orEmpty()
            findViewById<TextView>(R.id.c_sales_article_form_wish_price).text =
                formModel.wishPrice.orEmpty()
            initBookStatus(formModel.status)


            // 자신이 작성한 글일 시 수정, 채팅, 관심 버튼 사라짐
            // 상대방 글
            uid = auth.currentUser?.uid!!   // 현재 유저 uid

            if (formModel.uid != uid) {
                // UI 업데이트
                findViewById<Button>(R.id.article_form_edit_btn).isVisible = false
                findViewById<AppCompatButton>(R.id.c_sales_article_chat_btn).isVisible = true
                findViewById<AppCompatButton>(R.id.c_sales_article_like).isVisible = true

                // 초기화 과정(init)
                val formRef= reference.getReference("like_list/$aid/")
                val heart= binding.cSalesArticleLike

                initRef = formRef.child("whoLike").child(uid).addValueEventListener(object: ValueEventListener{
                    @SuppressLint("NewApi")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        when(snapshot.value as? Boolean){
                            true-> heart.background.setTint(getColor(R.color.red))
                            false-> heart.background.setTint(getColor(R.color.white))
                            else -> return
                        }
                        formRef.removeEventListener(initRef)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })


            heart.setOnClickListener {
                formRef.child("whoLike").child(uid).addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        @SuppressLint("NewApi")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val isLiked = snapshot.value as? Boolean
                            if (isLiked == true) {
                                //likeRef.child(uid).removeValue()

                                formRef.child("whoLike/$uid").setValue(null)
                                heart.background.setTint(getColor(R.color.white))
                                formRef.child("likeCount").setValue(ServerValue.increment(-1))
                            } else {
                                formRef.child("whoLike/$uid").setValue(true)
                                heart.background.setTint(getColor(R.color.red))
                                formRef.child("likeCount").setValue(ServerValue.increment(1))
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            } else {
                // 자신의 글
                findViewById<Button>(R.id.article_form_edit_btn).isVisible = true
                findViewById<AppCompatButton>(R.id.c_sales_article_chat_btn).isVisible = false
                findViewById<AppCompatButton>(R.id.c_sales_article_like).isVisible = false
                binding.bookStatusSpinner.isVisible = true
            }
        }

    private fun initBookStatus(status: String?) {
        when(status){
            "sale"->{
                binding.bookStatusText.isVisible= false
                binding.bookStatusText2.isVisible= false
            }
            "reserve"->{
                binding.bookStatusText.isVisible= true
                binding.bookStatusText2.isVisible= false
            }
            "sold"->{
                binding.bookStatusText.isVisible= false
                binding.bookStatusText2.isVisible= true
            }

        }
        val cformModel= formModel.copy(status="$status")
        FirebaseDatabase.getInstance().reference
            .child("sell_list/$aid/")
            .setValue(cformModel)

    }

    override fun onResume() {
            super.onResume()
            isBusy = false
        }

        private fun setupIndicators(count: Int) {
            val indicators: Array<ImageView?> = arrayOfNulls(count)
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
        inner class ImageSliderAdapter(context: Context, sliderImage: ArrayList<String?>) :
            RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {
            private val context: Context
            private val sliderImage: ArrayList<String?>

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
                    mImageView.setOnClickListener {
                        val intent =
                            Intent(this@CompletedSalesArticleForm, ZoomInSlider::class.java)
                        intent.putExtra("formModel", formModel)
                        intent.putExtra("photos", itemCount)
                        startActivity(intent)
                    }
                }

                fun bindSliderImage(imageURL: String?) {
                    Glide.with(context)
                        .load(imageURL)
                        .into(mImageView)
                }

            }
        }


    }











