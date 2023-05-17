package com.example.usedbookmarket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.usedbookmarket.fragment.ChatFragment.RecyclerViewAdapter
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


class AlertHistoryActivity : AppCompatActivity() {
    private val articleFormList= ArrayList<ArticleForm>()
    private val aidStringList= ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        findViewById<AppCompatButton>(R.id.books_you_have_backButton).setOnClickListener {
            onBackPressed()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.notification_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerViewAdapter()
    }
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        private val chatModelList = ArrayList<ChatModel>()
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
                    chatModelList.clear()
                    for (data in snapshot.children) {
                        val cm = data.getValue<ChatModel>()!!
                        chatModelList.add(cm)
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

            return CustomViewHolder(LayoutInflater.from(this@AlertHistoryActivity).inflate(R.layout.item_chat, parent, false))
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
                val intent= Intent(this@AlertHistoryActivity, ZoomImageActivity::class.java)
                intent.putExtra("formImage", articleFormList[position].coverSmallUrl)
                startActivity(intent)
            }



            var destinationUid: String? = null

            //채팅방에 있는 유저 모두 체크
            for (user in chatModelList[position].users.keys) {
                if (user != uid) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }
            val ref= FirebaseDatabase.getInstance().reference

            var friendName= ""

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
                    friendName= friend?.name.toString()
                }
            })
            //메세지 내림차순 정렬 후 마지막 메세지의 키값을 가져옴
            val commentMap = TreeMap<String, ChatModel.Comment>(Collections.reverseOrder())
            commentMap.putAll(chatModelList[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            holder.textView_lastMessage.text = chatModelList[position].comments[lastMessageKey]?.message

            // 임시 만든 알림 메시지
            val lastMsg= chatModelList[position].comments[lastMessageKey]?.message
            sendNotification(friendName, lastMsg.toString())



            //채팅창 선택 시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(this@AlertHistoryActivity, MessageActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])

                val likeForm: ArticleForm = articleFormList[position]
                intent.putExtra("formModel", likeForm)
                this@AlertHistoryActivity.startActivity(intent)
            }
        }
        override fun getItemCount(): Int {
            return chatModelList.size
        }
    }

    // 알림을 생성하는 메서드
    private fun sendNotification(title: String, body: String) {
        val notifyId = (System.currentTimeMillis() / 7).toInt()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                notifyId,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )


        val channelId = getString(R.string.channel_name)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.rightbubble)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelId,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notifyId, notificationBuilder.build())
    }
}