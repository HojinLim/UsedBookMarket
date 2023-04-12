package com.example.usedbookmarket

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    companion object {
        private const val OLD = 1
        private const val NEW = 2
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        auth= FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.testText)?.text= auth.currentUser?.email.toString()

//        임의로 fragment 화면으로 이동
        findViewById<AppCompatButton>(R.id.shortCutButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        //  임의로 test 화면으로 이동
        findViewById<AppCompatButton>(R.id.shortCutButton2).setOnClickListener {
            val intent= Intent(this, NotePad::class.java)
            intent.putExtra("title", "say my name")
            intent.putExtra("content", "Super Hojin?")

            startActivity(intent)
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