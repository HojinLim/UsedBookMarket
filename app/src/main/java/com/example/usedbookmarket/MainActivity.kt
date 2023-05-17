package com.example.usedbookmarket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.usedbookmarket.fragment.AccountFragment
import com.example.usedbookmarket.fragment.ChatFragment
import com.example.usedbookmarket.fragment.HomeFragment
import com.example.usedbookmarket.model.ChatModel
import com.example.usedbookmarket.model.Friend
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

private lateinit var homeFragment: HomeFragment
private lateinit var chatFragment: ChatFragment
private lateinit var accountFragment: AccountFragment

private val ref = FirebaseDatabase.getInstance().reference
private var chatRoomUid : String? = null
private var destinationUid : String? = null
private val comments = java.util.ArrayList<ChatModel.Comment>()
class MainActivity : AppCompatActivity() {
    private var uid : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeFragment = HomeFragment.newInstance()
        changeFragment(homeFragment)

        uid = Firebase.auth.currentUser?.uid.toString()

        initFragments()

        getHashKey()

        sayHelloPopUp()
       // detectMessage()
        getMessageList()
        checkChatRoom()
    }
    private fun checkChatRoom(){

        ref.child("chatrooms/").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (item in snapshot.children){
//                        println(item)
//                        val chatModel = item.getValue<ChatModel>()
//                        if(chatModel?.users!!.containsKey(destinationUid)){
//                            chatRoomUid = item.key
//
//                        }
//                    }
//                    Toast.makeText(this@MainActivity, "현 유저의 방이 있다.", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private var friend: Friend? = null

//    init {
//        ref.child("users").child(destinationUid.toString())
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    friend = snapshot.getValue<Friend>()
////                    binding.itemChatListNickName.text =
////                        friend?.name
//                    getMessageList()
//                }
//            })
//    }
    fun getMessageList() {
        ref.child("chatrooms/").child(chatRoomUid.toString()).child("comments")
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
                    //notifyDataSetChanged()
                    //메세지를 보낼 시 화면을 맨 밑으로 내림
                    //recyclerView?.scrollToPosition(comments.size - 1)
                }
            })
    }

    // 메시지 수신 탐지
    private fun detectMessage() {
        val ref= FirebaseDatabase.getInstance().reference
        var title = ""
        var content= ""

        uid = Firebase.auth.currentUser?.uid.toString()
        println(uid)
        val chatModelList = ArrayList<ChatModel.Comment>()

        //채팅방/현재유저id/true 일시 탐색
        ref.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatModelList.clear()
                for (data in snapshot.children) {
                    val cm = data.getValue<ChatModel.Comment>()!!
                    chatModelList.add(cm)
                    println(data)

                }
            }
        })
        //메세지 내림차순 정렬 후 마지막 메세지의 키값을 가져옴
//        val commentMap = TreeMap<String, ChatModel.Comment>(Collections.reverseOrder())
//        commentMap.putAll(chatModelList[position].comments)


        title = chatModelList.last().message.toString()
        content = chatModelList.last().message.toString()
        sendNotification(title, content)
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

    private fun sayHelloPopUp() {
        val uid= Firebase.auth.currentUser?.uid

        if(uid != null){

            Firebase.database.getReference("users").child(uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue<Friend>()
                    Toast.makeText(this@MainActivity, userProfile?.name+"님 환영합니다!",Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Toast.makeText(this@MainActivity, "로그인 정보가 없습니다!",Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun getHashKey() {
        var packageInfo: PackageInfo = PackageInfo()
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        for (signature: Signature in packageInfo.signatures) {
            try {
                var md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KEY_HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KEY_HASH", "Unable to get MessageDigest. signature = " + signature, e)
            }
        }
    }

    // 각 프래그먼트 대입 및 초기값, 메뉴 클릭에 따른 변경
    private fun initFragments() {
        changeFragment(homeFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    homeFragment= HomeFragment.newInstance()
                    changeFragment(homeFragment)
                }
                R.id.chat -> {
                    chatFragment= ChatFragment.newInstance()
                    changeFragment(chatFragment)
                }
                R.id.myPage -> {
                    accountFragment= AccountFragment.newInstance()
                    changeFragment(accountFragment)
                }
            }
            true
        }
    }

    private fun changeFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .apply{
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }
    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "default_channel_id"
    }
}