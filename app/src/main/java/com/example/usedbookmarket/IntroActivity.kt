package com.example.usedbookmarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        var handler = Handler()
        handler.postDelayed( {
            var intent = Intent( this, StartActivity::class.java)
            startActivity(intent)
        }, 3000)    // 3 초후 00 액티비티로 이동


    }

    override fun onPause() {
        super.onPause()
        finish()
      }
    }
