package com.example.usedbookmarket

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.example.usedbookmarket.model.Friend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

lateinit var database: DatabaseReference
class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var imageUri: Uri? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.data //이미지 경로 원본
                findViewById<ImageView>(R.id.signup_profile).setImageURI(imageUri) //이미지 뷰를 바꿈
                Log.d("이미지", "성공")
            } else {
                Log.d("이미지", "실패")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth
        database = Firebase.database.reference

        // 외부 저장소 권한 요청
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
//        initSignUpButton()

        val signUpButton = findViewById<AppCompatButton>(R.id.signUpButton)
        val userProfile = findViewById<ImageView>(R.id.signup_profile)

        var profileCheck = false

        userProfile.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
            //다른 이미지 로드 법 https://gogorchg.tistory.com/entry/Android-ACTIONPICK-%EC%82%AC%EC%9A%A9-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EA%B0%80%EC%A0%B8%EC%98%A4%EA%B8%B0
            profileCheck = true
        }

        signUpButton.setOnClickListener {
            val name = getInputName()
            val email = getInputEmail()
            val password = getInputPassword()

            if (email.isEmpty() && password.isEmpty() && name.isEmpty() && profileCheck) {
                Toast.makeText(this, "아이디와 비밀번호, 프로필 사진을 제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
                Log.d("Email", "$email, $password")
            } else {
                if (!profileCheck) {
                    Toast.makeText(this, "프로필사진을 등록해주세요.", Toast.LENGTH_SHORT).show()
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = Firebase.auth.currentUser
                            val userId = user?.uid
                            val userIdSt = userId.toString()

                            FirebaseStorage.getInstance()
                                .reference.child("userImages").child("$userIdSt/photo")
                                .putFile(imageUri!!).addOnSuccessListener {
                                    var userProfile: Uri? = null
                                    FirebaseStorage.getInstance().reference.child("userImages")
                                        .child("$userIdSt/photo").downloadUrl
                                        .addOnSuccessListener {
                                            userProfile = it
                                            Log.d("이미지 URL", "$userProfile")
                                            val friend = Friend(
                                                email.toString(),
                                                name.toString(),
                                                userProfile.toString(),
                                                userIdSt
                                            )
                                            database.child("users").child(userId.toString())
                                                .setValue(friend)
                                        }
                                }

                            Toast.makeText(
                                this,
                                "회원가입에 성공하였습니다! 로그인 버튼을 눌러 로그인해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "$userId")
                            startActivity(Intent(this, LoginActivity::class.java))
                            // finish()
                        } else {
                            Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }

        }

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
           // reload();
        }
    }
    private fun getInputName(): String {
        return findViewById<EditText>(R.id.inputNameEditText).text.toString()
    }

    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.inputEmailEditText).text.toString()
    }

    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.inputPasswordEditText).text.toString()
    }
}