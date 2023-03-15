package com.example.usedbookmarket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.usedbookmarket.databinding.ActivitiySalesArticleFormBinding
import com.example.usedbookmarket.model.ArticleForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SalesArticleFormActivity2: AppCompatActivity() {
    private lateinit var formModel: ArticleForm
    private var database = Firebase.database.reference
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitiySalesArticleFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitiySalesArticleFormBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val reference = Firebase.database

        // CompletedSaleForm에서 온 인텐트
        formModel = intent.getParcelableExtra("formModel")!!
        initView()

        // 완료 버튼을 누를시
        binding.articleFormEditBtn.setOnClickListener {

            val changedFormModel =
                formModel.copy(formTitle= binding.articleFormFormTitle.text.toString(),
                wishPrice = binding.articleFormWishPrice.text.toString(),
                formDescription = binding.articleFormDescription.text.toString())

            reference.getReference("sell_list").child(changedFormModel.aid.toString()).setValue(changedFormModel)
            Toast.makeText(this, "수정 완료하였습니다!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initView() {
        binding.articleFormDetailTitle.text= formModel.title
        binding.articleFormFormTitle.setText(formModel.formTitle)
        binding.articleFormWishPrice.setText(formModel.wishPrice)
        binding.articleFormDiscount.text = formModel.priceSales.orEmpty()
        binding.articleFormDescription.setText(formModel.formDescription)

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
    }
}

/*
binding.articleFormCompleteBtn.setOnClickListener {
    val articleModel = ArticleForm(
        auth.currentUser?.uid,
        formModel.id,
        formModel.title,
        formModel.description,
        formModel.priceSales,
        formModel.coverSmallUrl,
        binding.articleFormFormTitle.text.toString(),
        binding.articleFormDescription.text.toString(),
        binding.articleFormWishPrice.text.toString()
    )
    reference.getReference("sell_list").push().setValue(articleModel)
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)

}


 */