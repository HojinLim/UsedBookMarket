package com.example.usedbookmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 회원가입 화면으로 이동
        findViewById<AppCompatButton>(R.id.goSignUpButton).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        initLoginButton()
        initEmailAndPasswordEditText()

    }
    // TODO 로그인 구현
    private fun initLoginButton(){
        val loginButton= findViewById<AppCompatButton>(R.id.login_loginButton)
        loginButton.setOnClickListener{
            val email= getInputEmail()
            val password= getInputPassword()


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        //finish()
                        startActivity(Intent(this, StartActivity::class.java))
                        Toast.makeText(this, "로그인 완료!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "잘못 입력하였거나 계정이 존재하지 않습니다!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun initEmailAndPasswordEditText(){
        val emailEditText= findViewById<EditText>(R.id.login_emailEditText)
        val passwordEditText= findViewById<EditText>(R.id.login_passEditText)
        val loginButton= findViewById<AppCompatButton>(R.id.login_loginButton)

        emailEditText.addTextChangedListener {
            val enable= emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled= enable
        }
        passwordEditText.addTextChangedListener {
            val enable= emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled= enable
        }
    }

    private fun getInputEmail(): String{
        return findViewById<EditText>(R.id.inputEmailEditText).text.toString().trim()

    }
    private fun getInputPassword(): String{
        return findViewById<EditText>(R.id.inputPasswordEditText).text.toString().trim()
    }
}