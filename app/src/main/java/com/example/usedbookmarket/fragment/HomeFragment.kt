package com.example.gitproject.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.gitproject.AppDatabase
import com.example.gitproject.DetailActivity
import com.example.gitproject.R
import com.example.gitproject.adapter.BookAdapter
import com.example.gitproject.adapter.HistoryAdapter
import com.example.gitproject.api.BookService
import com.example.gitproject.databinding.ActivityMainBinding
import com.example.gitproject.databinding.FragmentHomeBinding
import com.example.gitproject.model.BestSellerDto
import com.example.gitproject.model.History
import com.example.gitproject.model.SearchBookDto
import com.example.usedbookmarket.R
import com.example.usedbookmarket.adapter.BookAdapter
import com.example.usedbookmarket.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var service: BookService

    private lateinit var db: AppDatabase

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)


        db = Room.databaseBuilder(
            requireActivity().applicationContext,
            AppDatabase::class.java,
            "historyDB"
        )
            .build()

        adapter = BookAdapter(clickListener = {
            val intent = Intent(requireActivity().applicationContext, DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
        historyAdapter = HistoryAdapter(historyDeleteClickListener = {
            deleteSearchKeyword(it)
        })


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(BookService::class.java)
        service.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object: Callback<BestSellerDto> {
                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {

                }

                override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {
                    if (response.isSuccessful.not()) {
                        return
                    }

                    response.body()?.let {
                        adapter.submitList(it.books)
                    }
                }

            })



        binding.bookRecyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        binding.bookRecyclerView.adapter = adapter

        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false

        }

        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }

            return@setOnTouchListener false
        }


        binding.historyRecyclerView.adapter = historyAdapter
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

    return view
    }

    private fun search(text: String) {


        service.getBooksByName(
            getString(R.string.interparkAPIKey),
            text
        )
            .enqueue(object : Callback<SearchBookDto> {
                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    hideHistoryView()
                }

                override fun onResponse(call: Call<SearchBookDto>, response: Response<SearchBookDto>) {

                    hideHistoryView()
                    saveSearchKeyword(text)

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

    private fun showHistoryView() {
        Thread(Runnable {
            db.historyDao()
                .getAll()
                .reversed()
                .run {
                    requireActivity().runOnUiThread {
                        binding.historyRecyclerView.isVisible = true
                        historyAdapter.submitList(this)
                    }
                }

        }).start()

    }

    private fun hideHistoryView() {
        binding.historyRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread(Runnable {
            db.historyDao()
                .insertHistory(History(null, keyword))
        }).start()
    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread(Runnable {
            db.historyDao()
                .delete(keyword)
            showHistoryView()
        }).start()
    }

    companion object {
        private const val TAG = "HomeFragment"
        private const val BASE_URL = "https://book.interpark.com/"
    }
}


