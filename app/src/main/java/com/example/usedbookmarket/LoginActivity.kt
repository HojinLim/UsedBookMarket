package com.example.usedbookmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 회원가입 화면으로 이동
        findViewById<AppCompatButton>(R.id.goSignUpButton).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        //TODO 로그인 구현
    }
}