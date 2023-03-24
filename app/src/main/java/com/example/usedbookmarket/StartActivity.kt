package com.example.usedbookmarket

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        auth= FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.testText)?.text= auth.currentUser?.email.toString()

//        임의로 만든 책목록 페이지 바로가기
        findViewById<AppCompatButton>(R.id.shortCutButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }



        // 회원가입 화면으로 이동
        findViewById<AppCompatButton>(R.id.goSignUpButton).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        //TODO 로그인 화면으로 이동
        findViewById<AppCompatButton>(R.id.goLoginButton).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}