package com.example.usedbookmarket.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.usedbookmarket.*
import com.example.usedbookmarket.adapter.ArticleAdapter
import com.example.usedbookmarket.adapter.HistoryAdapter
import com.example.usedbookmarket.databinding.FragmentHomeBinding
import com.example.usedbookmarket.model.ArticleForm
import com.example.usedbookmarket.model.History
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment: androidx.fragment.app.Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    companion object {
        private const val TAG = "HomeFragment"
        private const val BASE_URL = "https://openapi.naver.com/"
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }
    // 1. Context를 받아올 변수 선언
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 Activity로 형변환하여 할당
        mainActivity = context as MainActivity
    }


    private lateinit var articleDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var db: AppDatabase

    private lateinit var recyclerView: RecyclerView
    private val articleFormList = mutableListOf<ArticleForm>()
    private val searchedArticleFormList = mutableListOf<ArticleForm>()
    private var foundKeyword: Boolean= false

    // private lateinit var service: BookAPI
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleForm: ArticleForm? = snapshot.getValue(ArticleForm::class.java)
            articleForm ?: return
            articleFormList.add(articleForm)
            articleAdapter.submitList(articleFormList)
            articleAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    // private lateinit var db: AppDatabase


    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "historyDB"
        )
            .build()

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 왜 해주는건지 잘 몰겠다
        //articleFormList.clear()


        binding.homeFloatBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AddBookActivity::class.java))
        }
        binding.homeNotificationButton.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
        recyclerView = binding.homeBookRecyclerView

        articleDB = Firebase.database.reference.child("sell_list")
        articleAdapter = ArticleAdapter(clickListener = {
            val intent = Intent(requireContext(), CompletedSalesArticleForm::class.java)
            intent.putExtra("formModel", it)
            startActivity(intent)
        })

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = articleAdapter

        articleDB.addChildEventListener(listener)

        // 히스토리
        historyAdapter = HistoryAdapter(historyDeleteClickListener = {
            deleteSearchKeyword(it)
        })
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->


            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                //search(binding.searchEditText.text.toString())

                saveSearchKeyword(binding.searchEditText.text.toString())
                showHistoryView()
                searchBooks(binding.searchEditText.text.toString())
                binding.searchEditText.text= null
                return@setOnKeyListener true
            }
            //foundKeyword != foundKeyword
            return@setOnKeyListener false

        }

        binding.homeSearchImg.setOnClickListener {
            hideHistoryView()

        }


        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }

            return@setOnTouchListener false
        }

        binding.historyRecyclerView.adapter = historyAdapter
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }



    private fun showHistoryView() {
        binding.historyRecyclerView.isVisible = true
        Thread(Runnable {
            db.historyDao()
                .getAll()
                .reversed()
                .run {
                    mainActivity.runOnUiThread {
                        historyAdapter.submitList(this)
                        Log.d("TAG", this.toString())
                    }
                }
        }).start()
    }
    private fun hideHistoryView() {
        binding.historyRecyclerView.isVisible = false
    }
    private fun searchBooks(keyword: String) {
        //foundKeyword != foundKeyword
        for(articleForm in articleFormList){
            if(articleForm.title.toString().contains(keyword))
                searchedArticleFormList.add(articleForm)
                //Toast.makeText(requireContext(), "Found!", Toast.LENGTH_SHORT).show()
        }
        hideHistoryView()
        articleAdapter.submitList(searchedArticleFormList)
        //articleAdapter.notifyDataSetChanged()
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

    override fun onResume() {
        super.onResume()

        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }

    /*
    private fun search(text: String) {

        service.getBooksByName(
            getString(R.string.naver_id),
            getString(R.string.naver_secret_key),
            text
        )
            .enqueue(object : Callback<SearchBooksDto> {
                override fun onFailure(call: Call<SearchBooksDto>, t: Throwable) {
                   // hideHistoryView()
                }

                override fun onResponse(call: Call<SearchBooksDto>, response: Response<SearchBooksDto>) {

                    //hideHistoryView()
                   // saveSearchKeyword(text)

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
*/


}




