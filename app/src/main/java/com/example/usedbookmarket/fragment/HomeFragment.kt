package com.example.usedbookmarket.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.usedbookmarket.*
import com.example.usedbookmarket.adapter.ArticleAdapter
import com.example.usedbookmarket.adapter.HistoryAdapter
import com.example.usedbookmarket.databinding.FragmentHomeBinding
import com.example.usedbookmarket.databinding.ItemHistoryBinding
import com.example.usedbookmarket.model.ArticleForm
import com.example.usedbookmarket.model.History
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class HomeFragment: androidx.fragment.app.Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    companion object {

        private const val TAG = "HomeFragment"
        private const val BASE_URL = "https://openapi.naver.com/"
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
        val diffUtil = object : DiffUtil.ItemCallback<History>() {
            override fun areContentsTheSame(oldItem: History, newItem: History) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: History, newItem: History) =
                oldItem.uid == newItem.uid
        }
    }
    // 1. Context를 받아올 변수 선언
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("TEST", "OnAttach")
        // 2. Context를 Activity로 형변환하여 할당
        mainActivity = context as MainActivity

        articleAdapter = ArticleAdapter(clickListener = {
            val intent = Intent(requireContext(), CompletedSalesArticleForm::class.java)
            intent.putExtra("formModel", it)
            startActivity(intent)
        })


    }


    private lateinit var articleDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var db: AppDatabase

    private lateinit var recyclerView: RecyclerView
    private val articleFormList = mutableListOf<ArticleForm>()
    private val searchedArticleFormList = mutableListOf<ArticleForm>()

    private val listener = object : ChildEventListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleForm: ArticleForm? = snapshot.getValue(ArticleForm::class.java)
            articleForm ?: return
            articleFormList.add(articleForm)

            articleAdapter.submitList(articleFormList)
            articleAdapter.notifyDataSetChanged()
            Log.d("TEST","onChildAdded")
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val articleForm: ArticleForm? = snapshot.getValue(ArticleForm::class.java)
            articleForm ?: return


//            articleFormList.add(articleForm)

            articleAdapter.run {
                submitList(articleFormList)
                notifyDataSetChanged()
            }
            Log.d("TEST","onChildChanged")
            Log.d("TEST",articleForm.toString())
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val articleForm: ArticleForm? = snapshot.getValue(ArticleForm::class.java)
            articleForm ?: return
            articleFormList.remove(articleForm)
            articleAdapter.submitList(articleFormList)
            articleAdapter.notifyDataSetChanged()
            Log.d("TEST","onChildRemoved")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TEST", "OnCreateView")
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "historyDB"
        ).build()

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.homeFloatBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AddBookActivity::class.java))
        }
        binding.homeNotificationButton.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
        recyclerView = binding.homeBookRecyclerView

        articleDB = Firebase.database.reference.child("sell_list")



        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = articleAdapter

        articleDB.addChildEventListener(listener)

        // 히스토리
        historyAdapter = HistoryAdapter(historyDeleteClickListener = {
            deleteSearchKeyword(it)
        })
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            val text= binding.searchEditText.text.toString()
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN && text.isNotEmpty()) {
                saveSearchKeyword(binding.searchEditText.text.toString())
                showHistoryView()
                searchBooks(text)
                binding.searchEditText.text= null
                return@setOnKeyListener true
            }
            //foundKeyword != foundKeyword
            return@setOnKeyListener false
        }
        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
                // 돋보기 -> 뒤로가기
                binding.homeSearchImg.setImageResource(R.drawable.ic_baseline_keyboard_arrow_left_24)
            }
            return@setOnTouchListener false
        }

        binding.homeSearchImg.setOnClickListener {
            //hideHistoryView()
            mainActivity.supportFragmentManager.beginTransaction()
                .apply{
                    replace(R.id.fragmentContainer, newInstance())
                    commit()
                }
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
//                        Log.d("TAG", this.toString())
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        Log.d("TEST", "OnResume")
        articleAdapter.notifyDataSetChanged()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TEST", "OnDestroyView")
        articleDB.removeEventListener(listener)
    }


    inner class HistoryAdapter(val historyDeleteClickListener: (String) -> (Unit)) : ListAdapter<History, HistoryAdapter.ViewHolder>(diffUtil) {

        inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(historyModel: History) {
                binding.historyKeywordTextView.text = historyModel.keyword
                binding.historyKeywordTextView.setOnClickListener {
                    searchBooks(historyModel.keyword!!)
                }
                binding.historyKeywordDeleteButton.setOnClickListener {
                    historyDeleteClickListener(historyModel.keyword.orEmpty())
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(currentList[position])
        }



    }





}




