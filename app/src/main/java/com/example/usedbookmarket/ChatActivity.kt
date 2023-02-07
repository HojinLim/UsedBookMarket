package com.example.usedbookmarket

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.adapter.ChatAdapter
import com.example.usedbookmarket.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChatActivity: AppCompatActivity() {
    val database= FirebaseDatabase.getInstance()
    val auth= FirebaseAuth.getInstance()
    private lateinit var chatListAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private val chatList= mutableListOf<Chat>()

    private val listener= object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val chat: Chat? = snapshot.getValue(Chat::class.java)
            chat ?: return
            chatList.add(chat)
            chatListAdapter.submitList(chatList)
            chatListAdapter.notifyDataSetChanged()
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

        override fun onCancelled(error: DatabaseError) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth ?: return
        val userName= auth.currentUser?.email
        val myRef= database.getReference("chat_list")

        val userID=intent.getStringExtra("msgSend")
        findViewById<TextView>(R.id.item_chatList_nickName).text= userID

        //val chat= Chat
        findViewById<EditText>(R.id.chat_msg).text
        findViewById<AppCompatButton>(R.id.chat_sendButton).setOnClickListener {
            //myRef.push().setValue(Chat)
        }

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = chatListAdapter

            myRef.addChildEventListener(listener)
        }

    }
