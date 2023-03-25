package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.usedbookmarket.databinding.ActivitiySalesArticleFormBinding
import com.example.usedbookmarket.model.ArticleForm
import com.example.usedbookmarket.model.Book
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class SalesArticleFormActivity:AppCompatActivity() {
    //private lateinit var database: Firebase
    private var database = Firebase.database.reference
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitiySalesArticleFormBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId", "ResourceAsColor", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val coverImageView: ImageView
        val reference = Firebase.database
        binding = ActivitiySalesArticleFormBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bookModel = intent.getParcelableExtra<Book>("bookModel")
        bookModel ?: return

        // 파일 생성한 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월 dd일")
        val curTime = dateFormat.format(Date(time)).toString()

        val articleKey= reference.getReference("sell_list").push().key

        binding.articleFormEditBtn.setOnClickListener {
            val articleModel = ArticleForm(
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
                curTime
            )
            // 랜덤 키 생성
            Toast.makeText(this, articleKey,Toast.LENGTH_SHORT).show()
            reference.getReference("sell_list/$articleKey").setValue(articleModel)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
        coverImageView = findViewById<ImageView>(R.id.article_form_coverImg)
        findViewById<TextView>(R.id.article_form_detail_title).text = bookModel?.title.orEmpty()
        Glide
            .with(coverImageView.context)
            .load(bookModel?.coverSmallUrl.orEmpty())
            .into(coverImageView)
        findViewById<TextView>(R.id.article_form_discount).text = bookModel?.priceSales.orEmpty()

    }
}
    private fun newOrEdit(){

    }

        /*

        findViewById<Button>(R.id.article_form_complete_btn).setOnClickListener {
            //val myRef = database.child(auth.currentUser.toString()).child("from")
            //myRef.setValue("John")
            saveSellData()
            

        }

    }

    private fun saveSellData() {
        database.child("sell_list").setValue(auth.currentUser?.email.toString())
        Log.d("TAG",database.child("message").parent.toString())
        //database.getReference("test").setValue("testcontent")

    }
    */
