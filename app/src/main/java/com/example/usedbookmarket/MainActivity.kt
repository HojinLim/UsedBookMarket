package com.example.usedbookmarket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.usedbookmarket.fragment.AccountFragment
import com.example.usedbookmarket.fragment.ChatFragment
import com.example.usedbookmarket.fragment.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initFragments()


    }

    // 각 프래그먼트 대입 및 초기값, 메뉴 클릭에 따른 변경
    private fun initFragments() {
        val homeFragment = HomeFragment()
        val chatFragment = ChatFragment()
        val accountFragment = AccountFragment()

        changeFragment(homeFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> changeFragment(homeFragment)
                R.id.chat -> changeFragment(chatFragment)
                R.id.myPage -> changeFragment(accountFragment)
            }
            true
        }
    }

    private fun changeFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .apply{
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }
}