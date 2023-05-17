package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.usedbookmarket.databinding.ActivityMessageBinding
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
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity: AppCompatActivity() {

    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomUid : String? = null
    private var destinationUid : String? = null

    private var uid : String? = null
    private var email : String? = null
    private var recyclerView : RecyclerView? = null

//    private lateinit var bookName: String


    private lateinit var binding: ActivityMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageView = binding.chatSendButton
        val editText = binding.chatMsg

        //메세지를 보낸 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        // 상대방 정보
        val formModel: ArticleForm = intent.getParcelableExtra("formModel")!!

        destinationUid = intent.getStringExtra("destinationUid")

        // 나의 정보
        uid = Firebase.auth.currentUser?.uid.toString()
        email = Firebase.auth.currentUser?.email
        recyclerView = binding.msgHistoryRecyclerView

        // 거래책 이미지 적용
        Glide.with(this)
            .load(formModel.coverSmallUrl)
            .apply(RequestOptions().circleCrop())
            .into(binding.messageImage)

        // 거래책 글 이미지 클릭시
        binding.messageImage.setOnClickListener {
            val intent = Intent(this, CompletedSalesArticleForm::class.java)
            intent.putExtra("formModel", formModel)
            startActivity(intent)
        }

        // 뒤로가기
        binding.booksYouHaveBackButton.setOnClickListener {
            onBackPressed()
        }

        // 거래 완료 버튼
        binding.messageCompleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this@MessageActivity)
            builder.setTitle("거래 완료")
                .setMessage("리뷰 작성 페이지로 갑니다")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
//                                resultText.text = "확인 클릭"

                        val intent = Intent(this, ReviewActivity::class.java)
                        intent.putExtra("formModel", formModel)
                        intent.putExtra("destinationUid", destinationUid)
                        startActivity(intent)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
//                                resultText.text = "취소 클릭"
                        return@OnClickListener
                    })
            // 다이얼로그를 띄워주기
            builder.show()


        }

        // 채팅하기 버튼 클릭
        imageView.setOnClickListener {
            Log.d("클릭 시 dest", "$destinationUid")
            val chatModel = ChatModel()


            chatModel.users[uid.toString()] = true
            chatModel.users[destinationUid!!] = true

            chatModel.aid= formModel.aid


            val comment = ChatModel.Comment(uid, editText.text.toString(), curTime)
            if(chatRoomUid == null){
                imageView.isEnabled = false
                fireDatabase.child("chatrooms/").push().setValue(chatModel).addOnSuccessListener {
                    //채팅방 생성
                    checkChatRoom()
                    //메세지 보내기
                    Handler().postDelayed({
                        println(chatRoomUid)
                        fireDatabase.child("chatrooms/").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                        binding.chatMsg.text = null


                    }, 1000L)
                    Log.d("chatUidNull dest", "$destinationUid")
                }
            }else{
                fireDatabase.child("chatrooms/").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                binding.chatMsg.text = null
                Log.d("chatUidNotNull dest", "$destinationUid")
            }
        }



        checkChatRoom()
        }
    private fun checkChatRoom(){
        fireDatabase.child("chatrooms/").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children){
                        println(item)
                        val chatModel = item.getValue<ChatModel>()
                        if(chatModel?.users!!.containsKey(destinationUid)){
                            chatRoomUid = item.key
                            binding.chatSendButton.isEnabled = true
                            recyclerView?.layoutManager = LinearLayoutManager(this@MessageActivity)
                            recyclerView?.adapter = RecyclerViewAdapter()
                        }
                    }
                }
            })
    }
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        private val comments = ArrayList<ChatModel.Comment>()
        private var friend: Friend? = null

        init {
            fireDatabase.child("users").child(destinationUid.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        friend = snapshot.getValue<Friend>()
                        binding.itemChatListNickName.text =
                            friend?.name
                        getMessageList()
                    }
                })
        }

        fun getMessageList() {
            fireDatabase.child("chatrooms/").child(chatRoomUid.toString()).child("comments")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        comments.clear()
                        for (data in snapshot.children) {
                            val item = data.getValue<ChatModel.Comment>()
                            comments.add(item!!)
                            println(comments)
                        }
                        notifyDataSetChanged()
                        //메세지를 보낼 시 화면을 맨 밑으로 내림
                        recyclerView?.scrollToPosition(comments.size - 1)
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

            return MessageViewHolder(view)
        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.textView_message.textSize = 20F
            holder.textView_message.text = comments[position].message
            holder.textView_time.text = comments[position].time
            if (comments[position].uid.equals(uid)) { // 본인 채팅
                holder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                holder.textView_name.visibility = View.INVISIBLE
                holder.layout_destination.visibility = View.INVISIBLE
                holder.layout_main.gravity = Gravity.RIGHT
            } else { // 상대방 채팅
                Glide.with(holder.itemView.context)
                    .load(friend?.profileImageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.imageView_profile)
                holder.textView_name.text = friend?.name
                holder.layout_destination.visibility = View.VISIBLE
                holder.textView_name.visibility = View.VISIBLE
                holder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                holder.layout_main.gravity = Gravity.LEFT
            }
        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView_message: TextView = view.findViewById(R.id.messageItem_textView_message)
            val textView_name: TextView = view.findViewById(R.id.messageItem_textview_name)
            val imageView_profile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
            val layout_destination: LinearLayout =
                view.findViewById(R.id.messageItem_layout_destination)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val textView_time: TextView = view.findViewById(R.id.messageItem_textView_time)

        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }
    init{
        //binding.itemChatListNickName
    }


    }
