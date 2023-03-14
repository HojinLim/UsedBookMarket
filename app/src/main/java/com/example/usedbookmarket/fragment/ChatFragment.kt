package com.example.usedbookmarket.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.usedbookmarket.MessageActivity
import com.example.usedbookmarket.R
import com.example.usedbookmarket.model.ChatModel
import com.example.usedbookmarket.model.Friend
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatFragment : Fragment(R.layout.fragment_chatlist) {
    /*
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
            val intent = Intent(requireContext(), MessageActivity::class.java)
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

     */
    companion object{
        fun newInstance() : ChatFragment {
            return ChatFragment()
        } // 프래그먼트 재생성(화면 회전과 같은)시 빈생성자가 있어야 한다?
    }
    private val fireDatabase = FirebaseDatabase.getInstance().reference

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chatlist, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chatlist_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewAdapter()

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        private val chatModel = ArrayList<ChatModel>()
        private var uid : String? = null
        private val destinationUsers : ArrayList<String> = arrayListOf()

        init {
            uid = Firebase.auth.currentUser?.uid.toString()
            println(uid)

            fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModel.clear()
                    for(data in snapshot.children){
                        chatModel.add(data.getValue<ChatModel>()!!)
                        println(data)
                    }
                    notifyDataSetChanged()
                }
            })
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.chat_item_imageview)
            val textView_title : TextView = itemView.findViewById(R.id.chat_textview_title)
            val textView_lastMessage : TextView = itemView.findViewById(R.id.chat_item_textview_lastmessage)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            var destinationUid: String? = null
            //채팅방에 있는 유저 모두 체크
            for (user in chatModel[position].users.keys) {
                if (!user.equals(uid)) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }
            fireDatabase.child("users").child("$destinationUid").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val friend = snapshot.getValue<Friend>()
                    Glide.with(holder.itemView.context).load(friend?.profileImageUrl)
                        .apply(RequestOptions().circleCrop())
                        .into(holder.imageView)
                    holder.textView_title.text = friend?.name
                }
            })
            //메세지 내림차순 정렬 후 마지막 메세지의 키값을 가져옴
            val commentMap = TreeMap<String, ChatModel.Comment>(Collections.reverseOrder())
            commentMap.putAll(chatModel[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            holder.textView_lastMessage.text = chatModel[position].comments[lastMessageKey]?.message

            //채팅창 선택 시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                context?.startActivity(intent)
            }
        }
        override fun getItemCount(): Int {
            return chatModel.size
        }
    }
}