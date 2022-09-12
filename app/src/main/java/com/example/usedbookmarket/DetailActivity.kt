package com.example.usedbookmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.adapter.BookAdapter
import com.example.usedbookmarket.databinding.ActivityDetailBinding
import com.example.usedbookmarket.model.Book

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var adapter: BookAdapter
    private val books: ArrayList<Book> = ArrayList()
//    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /*
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    */

        val bookModel = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTextView.text = bookModel?.title.orEmpty()

        Glide
            .with(binding.detailCoverImageView.context)
            .load(bookModel?.coverSmallUrl.orEmpty())
            .into(binding.detailCoverImageView)

        binding.descriptionTextView.text = bookModel?.description.orEmpty()

        /*
        Thread {
            val review = db.reviewDao().getOne(bookModel?.id.orEmpty())
            runOnUiThread {
                binding.reviewEditText.setText(review?.review.orEmpty())
            }
        }.start()
 */
        adapter= BookAdapter {

        }

        /*
        val bookRecyclerView= findViewById<RecyclerView>(R.id.savedBookRecyclerView)
        bookRecyclerView.layoutManager = LinearLayoutManager(this)
        bookRecyclerView.adapter = adapter
 */
        binding.saveButton.setOnClickListener {
            if (bookModel != null) {
                books.add(bookModel)
            }
            adapter.submitList(books)

            /*
            Thread {
                db.reviewDao().saveReview(
                    Review(
                        bookModel?.id.orEmpty(),
                        binding.reviewEditText.text.toString()
                    )
                )

            }.start()

             */
        }


        }
    }