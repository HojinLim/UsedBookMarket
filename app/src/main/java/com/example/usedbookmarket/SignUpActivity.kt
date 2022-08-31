package com.example.usedbookmarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth= Firebase.auth
        initSignUpButton()
    }
    private fun initSignUpButton(){
        val signUpButton= findViewById<AppCompatButton>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email= getInputEmail()
            val password= getInputPassword()
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "회원가입에 성공하였습니다! 로그인 버튼을 눌러 로그인해주세요.", Toast.LENGTH_SHORT).show()
                        // finish()
                    }else {
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun getInputEmail(): String{
        return findViewById<EditText>(R.id.inputEmailEditText).text.toString()
    }
    private fun getInputPassword(): String{
        return findViewById<EditText>(R.id.inputPasswordEditText).text.toString()
    }
}