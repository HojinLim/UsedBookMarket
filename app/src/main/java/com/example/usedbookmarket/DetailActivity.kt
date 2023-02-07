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

       // myRef.addChildEventListener(listener)

        val bookModel = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTextView.text = bookModel?.title.orEmpty()

        Glide
            .with(binding.detailCoverImageView.context)
            .load(bookModel?.coverSmallUrl.orEmpty())
            .into(binding.detailCoverImageView)

        binding.detailDescriptionTextView.text = bookModel?.description.orEmpty()
        binding.detailPrice.text= bookModel?.priceSales.orEmpty()
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
        binding.detailSaveButton.setOnClickListener {
            val intent = Intent(this, SalesArticleFormActivity::class.java)
            intent.putExtra("bookModel", bookModel)
            startActivity(intent)
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