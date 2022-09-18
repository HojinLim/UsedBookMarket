package com.example.usedbookmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
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
        initEmailAndPasswordEditText()
    }
    private fun initSignUpButton(){
        val signUpButton= findViewById<AppCompatButton>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email= getInputEmail()
            val password= getInputPassword()
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "completed to sign up.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        // finish()
                    }else {
                        Toast.makeText(this, "failed to sign up!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun initEmailAndPasswordEditText(){
        val emailEditText= findViewById<EditText>(R.id.inputEmailEditText)
        val passwordEditText= findViewById<EditText>(R.id.inputPasswordEditText)
        val signupButton= findViewById<AppCompatButton>(R.id.signUpButton)

        emailEditText.addTextChangedListener {
            val enable= emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            signupButton.isEnabled= enable
        }
        passwordEditText.addTextChangedListener {
            val enable= emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            signupButton.isEnabled= enable
        }
    }
    private fun getInputEmail(): String{
        return findViewById<EditText>(R.id.inputEmailEditText).text.toString()
    }
    private fun getInputPassword(): String{
        return findViewById<EditText>(R.id.inputPasswordEditText).text.toString()
    }
}