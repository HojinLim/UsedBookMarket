package com.example.usedbookmarket

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // ----
        // 위치 권한 받기


        /*
        try {
            address = g.getFromLocation(lat, lng, 10)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("test", "입출력오류")
        }
        if (address != null) {
            if (address.size() === 0) {
                txt.setText("주소찾기 오류")
            } else {
                Log.d("찾은 주소", address.get(0).toString())
                txt.setText(address.get(0).getAddressLine(0))
            }
        }
         */

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }else{
            val button= findViewById<Button>(R.id.button)
            val textView= findViewById<TextView>(R.id.textView)

            button.setOnClickListener {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location ->
                        textView.text= location.toString()
                        val la= location.latitude
                        val lo= location.longitude
                        val g= Geocoder(this)


                        val address = g.getFromLocation(la, lo, 10)
                        if (address != null) {
                            if (address.size == 0) {
                                textView.text = "주소 찾기 오류"
                            } else {
                                Log.d("찾은 주소", address[0].toString())
                                textView.text = (address[0].getAddressLine(0))
                            }
                        }
                    }

            }
        }

        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            // 위치정보 설정 Intent

            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

        }





        // ----

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