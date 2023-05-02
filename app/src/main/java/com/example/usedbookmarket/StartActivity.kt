package com.example.usedbookmarket

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth


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

        auth= FirebaseAuth.getInstance()

        //        임의로 fragment 화면으로 이동
        findViewById<AppCompatButton>(R.id.shortCutButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

        }
        findViewById<TextView>(R.id.testText)?.text = auth.currentUser?.email
        // 회원가입 화면으로 이동
        findViewById<AppCompatButton>(R.id.goSignUpButton).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // 로그인 화면으로 이동
        findViewById<AppCompatButton>(R.id.goLoginButton).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // test2 버튼
        findViewById<AppCompatButton>(R.id.shortCutButton3).setOnClickListener {

        }




        findViewById<AppCompatButton>(R.id.shortCutButton2).setOnClickListener {
        }

        // ----------------------- 밑부터 연습장 ------------------------


        //        Toast.makeText(this@StartActivity, isRed.toString(),Toast.LENGTH_SHORT).show()
        // test1 버튼


    }
}