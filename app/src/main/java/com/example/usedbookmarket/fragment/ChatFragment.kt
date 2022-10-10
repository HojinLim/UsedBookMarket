package com.example.usedbookmarket.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.DetailActivity
import com.example.usedbookmarket.R
import com.example.usedbookmarket.adapter.ChatlistAdapter
import com.example.usedbookmarket.model.Book

class ChatFragment : Fragment(R.layout.fragment_chatlist) {
    private lateinit var adapter: ChatlistAdapter

    //private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val v: View = inflater.inflate(R.layout.fragment_chatlist, container, false)

        recyclerView = v.findViewById(R.id.chatlist_recyclerView)

        adapter = ChatlistAdapter(clickListener = {
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        adapter.submitList(
            listOf(
                Book(
                    "9788993827446",
                    "DDD Start!",
                    "DDD의 핵심 개념을 배우고",
                    "3000",
                    "https://shopping-phinf.pstatic.net/main_3245626/32456266806.20220527031023.jpg",
                    ""
                ),
                Book(
                    "9791162245385",
                    "도메인 주도 개발 시작하기",
                    "가장 쉽게 배우는 도메인 주도 설계 입문서!",
                    "25200",
                    "https://shopping-phinf.pstatic.net/main_3243631/32436316743.20220527044029.jpg",
                    ""
                )
            )
        )
        return v
    }
}