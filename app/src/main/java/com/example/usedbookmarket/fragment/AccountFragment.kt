package com.example.usedbookmarket.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.usedbookmarket.BooksYouHaveActivity
import com.example.usedbookmarket.BooksYouLikeActivity
import com.example.usedbookmarket.BooksYouSellActivity
import com.example.usedbookmarket.R
import com.example.usedbookmarket.StartActivity
import com.example.usedbookmarket.databinding.FragmentAccountBinding
import com.example.usedbookmarket.model.Friend
import com.example.usedbookmarket.uid
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class AccountFragment : Fragment(R.layout.fragment_account) {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var user: FirebaseAuth
    companion object{
        private var imageUri : Uri? = null
        private val fireStorage = FirebaseStorage.getInstance().reference
        private val fireDatabase = FirebaseDatabase.getInstance().reference
        private val user = Firebase.auth.currentUser

        fun newInstance() : AccountFragment {
            return AccountFragment()
        }

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext= context
    }

    private var mContext: Context? = null
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                imageUri = result.data?.data //이미지 경로 원본
                view?.findViewById<ImageView>(R.id.account_profile_pic)?.setImageURI(imageUri) //이미지 뷰를 바꿈

                //기존 사진을 삭제 후 새로운 사진을 등록
                fireStorage.child("userImages/$uid/photo").delete().addOnSuccessListener {
                    fireStorage.child("userImages/$uid/photo").putFile(imageUri!!).addOnSuccessListener {
                        fireStorage.child("userImages/$uid/photo").downloadUrl.addOnSuccessListener {
                            val photoUri : Uri = it
                            println("$photoUri")
                            fireDatabase.child("users/$uid/profileImageUrl").setValue(photoUri.toString())
                            Toast.makeText(requireContext(), "프로필사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Log.d("이미지", "성공")
            }
            else{
                Log.d("이미지", "실패")
            }
        }

    @Synchronized
    fun getInstance(): Context? {
        return mContext
    }
    init {
        uid = Firebase.auth.currentUser?.uid.toString()

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        if(mContext!= null) mContext= requireContext()


        val auth= FirebaseAuth.getInstance()


        binding.accountUserEmail.text= auth.currentUser?.email

        val photo = binding.accountProfilePic

        val email = binding.accountUserEmail
        val name = binding.accountUserName
        val button = binding.accountChangeNameBtn

        //프로필 구현
        fireDatabase.child("users").child(uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue<Friend>()
                println(userProfile)
                Glide.with(mContext!!).load(userProfile?.profileImageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(photo)
                email.text = userProfile?.email
                name.setText(userProfile?.name)
            }
        })
        //프로필사진 바꾸기
        photo.setOnClickListener{
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }
        button.setOnClickListener{
            if(name?.text!!.isNotEmpty()) {
                fireDatabase.child("users/$uid/name").setValue(name.text.toString())
                name.clearFocus()
                Toast.makeText(requireContext(), "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        initIntentActivity()

        // 로그아웃 버튼 클릭
        binding.accountLogoutBtn.setOnClickListener {
            if(auth.currentUser != null) auth.signOut()
                Toast.makeText(mContext, "로그아웃 완료",Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), auth.currentUser?.uid.toString(),Toast.LENGTH_SHORT).show()
                val intent= Intent(requireContext(), StartActivity::class.java)
                startActivity(intent)
        }


        // 계정 삭제
        binding.accountDelUser.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val input = EditText(requireContext())
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            builder.setView(input)

            builder.setTitle("계정 삭제하기")
                .setMessage("계정 확인을 위해 비밀번호를 입력해주시오.")
                .setView(input)
                .setPositiveButton("입력완료",
                    DialogInterface.OnClickListener { dialog, id ->
                        val user = FirebaseAuth.getInstance().currentUser
                        val credential = EmailAuthProvider.getCredential("${user?.email}", "${input.text}")
                        user?.reauthenticate(credential)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // 사용자 인증이 성공하면 사용자 계정을 삭제합니다.
                                    user.delete()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                auth.signOut() // 로그아웃
                                                val intent= Intent(requireContext(), StartActivity::class.java)
                                                startActivity(intent)
                                                Toast.makeText(requireContext(), "삭제 완료!", Toast.LENGTH_SHORT).show()
                                                Log.d(TAG, "User account deleted.")
                                            } else {
                                                Log.e(TAG, "Failed to delete user account.", task.exception)
                                                Toast.makeText(requireContext(), "삭제 실패!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    Log.e(TAG, "Failed to reauthenticate user.", task.exception)
                                }
                            }

                        Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                        //                                resultText.text = "취소 클릭"
                        return@OnClickListener
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }

        return binding.root
}

    private fun initIntentActivity(){
        binding.accountListBooksYouHaveBtn.setOnClickListener {
            startActivity(Intent(requireContext(), BooksYouHaveActivity::class.java))
        }
        binding.accountListBooksYouSellBtn.setOnClickListener {
            startActivity(Intent(requireContext(), BooksYouSellActivity::class.java))
        }
        binding.accountListBooksYouLikeBtn.setOnClickListener {
            startActivity(Intent(requireContext(), BooksYouLikeActivity::class.java))
        }
    }

}