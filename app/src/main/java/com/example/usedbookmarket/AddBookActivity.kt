package com.example.usedbookmarket

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.adapter.BookAdapter
import com.example.usedbookmarket.api.BookAPI
import com.example.usedbookmarket.databinding.ActivityAddBookBinding
import com.example.usedbookmarket.databinding.ActivityDetailBinding
import com.example.usedbookmarket.databinding.FragmentHomeBinding
import com.example.usedbookmarket.model.SearchBooksDto
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddBookActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAddBookBinding
    private lateinit var adapter: BookAdapter

    private lateinit var service: BookAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            service = retrofit.create(BookAPI::class.java)


            adapter= BookAdapter(clickListener = {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("bookModel", it)
                startActivity(intent)
            })

            binding.addBookRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.addBookRecyclerView.adapter = adapter

            binding.addBookSearchEditText.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    search(binding.addBookSearchEditText.text.toString())
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false

            }

        }
        private fun search(text: String) {

            service.getBooksByName(
                getString(R.string.naver_id),
                getString(R.string.naver_secret_key),
                text
            )
                .enqueue(object : Callback<SearchBooksDto> {
                    override fun onFailure(call: Call<SearchBooksDto>, t: Throwable) {

                    }

                    override fun onResponse(call: Call<SearchBooksDto>, response: Response<SearchBooksDto>) {


                        if (response.isSuccessful.not()) {
                            return
                        }

                        response.body()
                            ?.let {
                                adapter.submitList(it.books)
                            }
                    }

                })
        }


        companion object {
            private const val TAG = "HomeFragment"
            private const val BASE_URL = "https://openapi.naver.com/"
        }
    }

