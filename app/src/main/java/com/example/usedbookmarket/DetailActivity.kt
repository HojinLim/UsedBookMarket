package com.example.usedbookmarket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.usedbookmarket.adapter.BookAdapter
import com.example.usedbookmarket.databinding.ActivityDetailBinding
import com.example.usedbookmarket.model.Book
import com.google.firebase.database.FirebaseDatabase


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var adapter: BookAdapter
    private val books: ArrayList<Book> = ArrayList()

    private var articleDB = FirebaseDatabase.getInstance().reference
    private var myRef = articleDB.child("text")


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bookModel = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTextView.text = bookModel?.title.orEmpty()

        Glide
            .with(binding.detailCoverImageView.context)
            .load(bookModel?.coverSmallUrl.orEmpty())
            .into(binding.detailCoverImageView)

        binding.detailCoverImageView.setOnClickListener {// 이미지 확대
            val intent= Intent(this@DetailActivity, ZoomImageActivity::class.java)
            intent.putExtra("formImage", bookModel?.coverSmallUrl)
            startActivity(intent)
        }

        binding.detailDescriptionTextView.text = bookModel?.description.orEmpty()
        binding.detailPrice.text= bookModel?.priceSales.orEmpty()

        adapter= BookAdapter {

        }


        binding.detailSaveButton.setOnClickListener {
            val intent = Intent(this, SalesArticleFormActivity2::class.java)
            intent.putExtra("bookModel", bookModel)
            startActivity(intent)

        }



        }
    }