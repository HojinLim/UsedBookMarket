package com.example.usedbookmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

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