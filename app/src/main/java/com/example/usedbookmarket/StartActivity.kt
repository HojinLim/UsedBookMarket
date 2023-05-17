package com.example.usedbookmarket

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage


//lateinit var db: FirebaseDatabase
lateinit var uid: String
//lateinit var btn1: AppCompatButton
//lateinit var btn2: AppCompatButton
//lateinit var btn3: AppCompatButton
//lateinit var ref: DatabaseReference
//private var isLiked: Boolean= false
class StartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        auth = FirebaseAuth.getInstance()
        val uid= auth.currentUser?.uid.toString()

        //        임의로 fragment 화면으로 이동
        findViewById<AppCompatButton>(R.id.shortCutButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

        }
        //findViewById<TextView>(R.id.testText)?.text = auth.currentUser?.email
        // 회원가입 화면으로 이동
        findViewById<AppCompatButton>(R.id.goSignUpButton).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // 로그인 화면으로 이동
        findViewById<AppCompatButton>(R.id.goLoginButton).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        val firebaseMessaging = FirebaseMessaging.getInstance()



        // ----------------------- 밑부터 연습장 ------------------------

        askNotificationPermission() //알림 권한
        // 클릭2
        findViewById<AppCompatButton>(R.id.shortCutButton3).setOnClickListener {
            //sendNotification("hi","bye")
            val builder = RemoteMessage.Builder("<SENDER_ID>@gcm.googleapis.com")
                .setMessageId("<RANDOM_UUID>")
                .addData(uid, uid)
                .addData("title", "my_title")
                .addData("message", "my_message")
                .setTtl(3600)

            firebaseMessaging.send(builder.build())
        }
        // 토큰 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d("myToken", token)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })




    }
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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
    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "default_channel_id"
    }


}








