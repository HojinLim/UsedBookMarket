package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.usedbookmarket.databinding.ActivitiySalesArticleFormBinding
import com.example.usedbookmarket.model.ArticleForm
import com.example.usedbookmarket.model.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SalesArticleFormActivity:AppCompatActivity() {
    //private lateinit var database: Firebase
    private var database = Firebase.database.reference
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitiySalesArticleFormBinding

    @SuppressLint("MissingInflatedId", "ResourceAsColor", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val coverImageView: ImageView
        val reference = Firebase.database
        binding = ActivitiySalesArticleFormBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //newOrEdit()



        val bookModel = intent.getParcelableExtra<Book>("bookModel")
        bookModel ?: return

        val articleKey= reference.getReference("sell_list").push().key

        binding.articleFormCompleteBtn.setOnClickListener {
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
                binding.articleFormWishPrice.text.toString()
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
