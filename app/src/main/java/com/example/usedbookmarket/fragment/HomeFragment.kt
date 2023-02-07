package com.example.usedbookmarket.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.AddBookActivity
import com.example.usedbookmarket.CompletedSalesArticleForm
import com.example.usedbookmarket.NotificationActivity
import com.example.usedbookmarket.R
import com.example.usedbookmarket.adapter.ArticleAdapter
import com.example.usedbookmarket.model.ArticleForm
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment: androidx.fragment.app.Fragment(R.layout.fragment_home) {

    companion object {
        private const val TAG = "HomeFragment"
        private const val BASE_URL = "https://openapi.naver.com/"
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }
    private lateinit var articleDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private lateinit var recyclerView: RecyclerView
    private val articleFormList = mutableListOf<ArticleForm>()

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

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_home, container, false)

        val database = Firebase.database
        articleFormList.clear()


        //Toast.makeText(requireContext(),Firebase.auth.currentUser?.uid,Toast.LENGTH_SHORT).show()

        v.findViewById<FloatingActionButton>(R.id.home_floatBtn).setOnClickListener {
            startActivity(Intent(requireContext(), AddBookActivity::class.java))
        }
        v.findViewById<AppCompatButton>(R.id.home_notificationButton).setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
        recyclerView = v.findViewById(R.id.home_bookRecyclerView)

        articleDB = Firebase.database.reference.child("sell_list")
        articleAdapter = ArticleAdapter(clickListener = {
            val intent = Intent(requireContext(), CompletedSalesArticleForm::class.java)
            intent.putExtra("formModel", it)
            startActivity(intent)
        })


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = articleAdapter

        articleDB.addChildEventListener(listener)


        return v
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




