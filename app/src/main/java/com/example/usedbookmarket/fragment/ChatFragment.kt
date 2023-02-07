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
import com.example.usedbookmarket.ChatActivity
import com.example.usedbookmarket.R
import com.example.usedbookmarket.adapter.ChatlistAdapter
import com.example.usedbookmarket.model.ChatList

class ChatFragment : Fragment(R.layout.fragment_chatlist) {
    companion object{
        fun newInstance() : ChatFragment {
            return ChatFragment()
        } // 프래그먼트 재생성(화면 회전과 같은)시 빈생성자가 있어야 한다?
    }
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
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("chatListModel", it)
            startActivity(intent)
        })

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        adapter.submitList(
            listOf(
                ChatList(
                    "https://sample-videos.com/img/Sample-jpg-image-1mb.jpg",
                    "Hojin",
                    "거래 희망합니다.",
                    "1일 전",
                    "https://shopping-phinf.pstatic.net/main_3245626/32456266806.20220527031023.jpg"
                ),
                ChatList(
                    "https://sample-videos.com/img/Sample-jpg-image-1mb.jpg",
                    "Jins-su",
                    "감사합니다!",
                    "3시간 전",
                    "https://shopping-phinf.pstatic.net/main_3243631/32436316743.20220527044029.jpg"
                )
            )
        )
        return v
    }
}