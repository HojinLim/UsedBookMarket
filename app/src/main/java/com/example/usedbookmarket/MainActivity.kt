package com.example.usedbookmarket

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.usedbookmarket.fragment.AccountFragment
import com.example.usedbookmarket.fragment.ChatFragment
import com.example.usedbookmarket.fragment.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

private lateinit var homeFragment: HomeFragment
private lateinit var chatFragment: ChatFragment
private lateinit var accountFragment: AccountFragment
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)






        homeFragment = HomeFragment.newInstance()
        changeFragment(homeFragment)

        initFragments()

        getHashKey()


    }


    private fun getHashKey() {
        var packageInfo: PackageInfo = PackageInfo()
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        for (signature: Signature in packageInfo.signatures) {
            try {
                var md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KEY_HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KEY_HASH", "Unable to get MessageDigest. signature = " + signature, e)
            }
        }
    }

    // 각 프래그먼트 대입 및 초기값, 메뉴 클릭에 따른 변경
    private fun initFragments() {
        changeFragment(homeFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    homeFragment= HomeFragment.newInstance()
                    changeFragment(homeFragment)
                }
                R.id.chat -> {
                    chatFragment= ChatFragment.newInstance()
                    changeFragment(chatFragment)
                }
                R.id.myPage -> {
                    accountFragment= AccountFragment.newInstance()
                    changeFragment(accountFragment)
                }
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
    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "default_channel_id"
    }
}