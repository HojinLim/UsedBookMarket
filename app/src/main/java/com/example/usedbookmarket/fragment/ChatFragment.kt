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
import com.example.usedbookmarket.ZoomImageActivity
import com.example.usedbookmarket.model.ArticleForm
import com.example.usedbookmarket.model.ChatModel
import com.example.usedbookmarket.model.Friend
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.Collections
import java.util.TreeMap

class ChatFragment : Fragment(R.layout.fragment_chatlist) {
    private val articleFormList= ArrayList<ArticleForm>()
    private val aidStringList= ArrayList<String>()
    private lateinit var adapter: RecyclerViewAdapter

    companion object{
        fun newInstance() : ChatFragment {
            return ChatFragment()
        } // 프래그먼트 재생성(화면 회전과 같은)시 빈생성자가 있어야 한다?
    }


    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
//        adapter.notifyDataSetChanged()
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
            val ref= FirebaseDatabase.getInstance().reference

            uid = Firebase.auth.currentUser?.uid.toString()
            println(uid)

            ref.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModel.clear()
                    for (data in snapshot.children) {
                        val cm = data.getValue<ChatModel>()!!
                        chatModel.add(cm)
                        println(data)

                        // article model을 가져온다
                        val aid = cm.aid.toString()
                        aidStringList.add(aid)

                        ref.child("sell_list/$aid")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val articleForm: ArticleForm =
                                        snapshot.getValue(ArticleForm::class.java)!!
                                    articleFormList.add(articleForm)

                                    notifyDataSetChanged()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                    }



                }
            })
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.chat_item_imageview)
            val coverImageView: ImageView = itemView.findViewById(R.id.item_chat_coverImage)
            val textView_title : TextView = itemView.findViewById(R.id.chat_textview_title)
            val textView_lastMessage : TextView = itemView.findViewById(R.id.chat_item_textview_lastmessage)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            Glide.with(holder.itemView.context).load(articleFormList[position].coverSmallUrl)
                .apply(RequestOptions().centerCrop())
                .into(holder.coverImageView)

            holder.coverImageView.setOnClickListener {// 이미지 확대
                val intent= Intent(requireContext(), ZoomImageActivity::class.java)
                intent.putExtra("formImage", articleFormList[position].coverSmallUrl)
                startActivity(intent)
            }



            var destinationUid: String? = null

            //채팅방에 있는 유저 모두 체크
            for (user in chatModel[position].users.keys) {
                if (user != uid) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }
            val ref= FirebaseDatabase.getInstance().reference

            ref.child("users").child("$destinationUid").addListenerForSingleValueEvent(object :
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

                val likeForm: ArticleForm= articleFormList[position]
                intent.putExtra("formModel", likeForm)
                context?.startActivity(intent)
            }
        }
        override fun getItemCount(): Int {
            return chatModel.size
        }
    }
}