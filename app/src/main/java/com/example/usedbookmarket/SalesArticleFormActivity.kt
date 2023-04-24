package com.example.usedbookmarket

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.databinding.ActivitiySalesArticleFormBinding
import com.example.usedbookmarket.model.ArticleForm
import com.example.usedbookmarket.model.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date

class SalesArticleFormActivity: AppCompatActivity() {

    private lateinit var reference: FirebaseDatabase
    private lateinit var formModel: ArticleForm
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitiySalesArticleFormBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter


    private var imageUri: Uri? = null

    private val images = arrayListOf<Uri?>()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.data //이미지 경로 원본
                images.add(imageUri)
                Toast.makeText(this, "Completed!", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
                Log.d("이미지", "성공")
            } else {
                Log.d("이미지", "실패")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitiySalesArticleFormBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        reference = Firebase.database

        // 어디서 intent 된지 flag
        val intentFrom= intent.getStringExtra("flag")

        if (intentFrom == "A") {
            // 처음 만드는 글
            Toast.makeText(this, "첨만드는글", Toast.LENGTH_SHORT).show()
            Log.d("TEST", intentFrom)
            initNewForm()

        } else if (intentFrom =="B") {
            // 로드하는 글
//            Toast.makeText(this, intentFrom, Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "된글", Toast.LENGTH_SHORT).show()
            Log.d("TEST", intentFrom)
            initOldForm()
        }

        // 공통 부분
        initSame()
    }

    private fun initSame() {
        binding.detailPhotoButton.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_GET_CONTENT)    //Intent.ACTION_GET_CONTENT
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }
        binding.booksYouHaveBackButton.setOnClickListener {
            onBackPressed()
        }

        val user= auth.currentUser
        val userId = user?.email
        val userIdSt = userId.toString()
        val aid= formModel.aid

        for((num, imageUri) in images.withIndex()) {

            FirebaseStorage.getInstance()
                .reference.child("userImages").child("formPhotos").child("$userIdSt/$aid/photo$num")
                .putFile(imageUri!!).addOnSuccessListener {

                }
        }

        recyclerView = binding.formRecyclerView

        adapter = RecyclerViewAdapter(images)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

    }

    // 글 새로 작성
    private fun initNewForm() {
        val bookModel = intent.getParcelableExtra<Book>("bookModel")
        bookModel ?: return

        // 파일 생성한 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월 dd일")
        val curTime = dateFormat.format(Date(time)).toString()

        // 랜덤 키 생성
        val articleKey = reference.getReference("sell_list").push().key

        // 책 제목 불러오기
        binding.articleFormDetailTitle.text= bookModel.title

        formModel = ArticleForm(
            articleKey,
            auth.currentUser?.uid,
            bookModel.id,
            bookModel.title,
            bookModel.description,
            bookModel.priceSales,
            bookModel.coverSmallUrl,
            binding.articleFormFormTitle.text.toString(),
            binding.articleFormDescription.text.toString(),
            binding.articleFormWishPrice.text.toString(),
            "false",
            curTime,
            auth.currentUser?.email,
            "sale"
        )


        // 글쓰기 완료 버튼
        binding.articleFormEditBtn.setOnClickListener {

            if(!isNotEmpty()) return@setOnClickListener

            formModel = ArticleForm(
                articleKey,
                auth.currentUser?.uid,
                bookModel.id,
                bookModel.title,
                bookModel.description,
                bookModel.priceSales,
                bookModel.coverSmallUrl,
                binding.articleFormFormTitle.text.toString(),
                binding.articleFormDescription.text.toString(),
                binding.articleFormWishPrice.text.toString(),
                "false",
                curTime,
                auth.currentUser?.email,
                "sale"
            )


            Toast.makeText(this, articleKey, Toast.LENGTH_SHORT).show()
            reference.getReference("sell_list/$articleKey").setValue(formModel)

            initSame()

            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }
    }
    // 글 쓰기 폼 예외 선별 함수
    private fun isNotEmpty(): Boolean {
        return if(images.isEmpty()){
            Toast.makeText(this, "사진을 등록해 주세요.",Toast.LENGTH_SHORT).show()
            false
        }else if(binding.articleFormFormTitle.text.isEmpty()) {
            Toast.makeText(this, "글 제목을 입력해 주세요.",Toast.LENGTH_SHORT).show()
            false
        }else if(binding.articleFormDescription.text.isEmpty()){
            Toast.makeText(this, "내용을 입력해 주세요.",Toast.LENGTH_SHORT).show()
            false
        }else if(binding.articleFormWishPrice.text.isEmpty()){
            Toast.makeText(this, "가격을 입력해 주세요.",Toast.LENGTH_SHORT).show()
            false
        }else {
            true
        }
    }

    private fun initOldForm() {
        // CompletedSaleForm에서 온 인텐트
        formModel = intent.getParcelableExtra("formModel")!!

        // 완료 버튼을 누를시
        binding.articleFormEditBtn.setOnClickListener {

            val changedFormModel =
                formModel.copy(formTitle= binding.articleFormFormTitle.text.toString(),
                    wishPrice = binding.articleFormWishPrice.text.toString(),
                    formDescription = binding.articleFormDescription.text.toString())
            reference.getReference("sell_list").child(changedFormModel.aid.toString()).setValue(changedFormModel)



            Toast.makeText(this, "수정 완료 하였습니다!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)


            startActivity(intent)
        }
        binding.articleFormDetailTitle.text= formModel.title
        binding.articleFormFormTitle.setText(formModel.formTitle)
        binding.articleFormWishPrice.setText(formModel.wishPrice)
        binding.articleFormDiscount.text = formModel.priceSales.orEmpty()
        binding.articleFormDescription.setText(formModel.formDescription)
    }

    inner class RecyclerViewAdapter(private val images: ArrayList<Uri?>) : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {
        init{

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            return CustomViewHolder(LayoutInflater.from(this@SalesArticleFormActivity).inflate(R.layout.item_book_photo, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.book_photo_coverImageView)
            val clearButton : ImageButton = itemView.findViewById(R.id.book_photo_clear_btn)

        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.imageView.setImageURI(images[position])
            holder.clearButton.setOnClickListener {
                deleteItem(position)
                Toast.makeText(this@SalesArticleFormActivity, "사진 삭제 완료!", Toast.LENGTH_SHORT).show()
            }
        }
        private fun deleteItem(position: Int){
            images.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position,images.size)
        }
//        public fun getPhotos(): ArrayList<Uri?>{
//            return images
//        }
        override fun getItemCount(): Int {
            return images.size
        }

    }
}


/*
        // 이미지 삽입
        Glide
            .with(applicationContext)
            .load(formModel?.coverSmallUrl.orEmpty())
            .into(binding.articleFormCoverImg)

        // 이미지를 클릭시 이미지 크게 보기
        binding.articleFormCoverImg.setOnClickListener {
            val intent= Intent(this, ZoomImageActivity::class.java)
            intent.putExtra("formImage", formModel.coverSmallUrl)
            startActivity(intent)
        }
        */
