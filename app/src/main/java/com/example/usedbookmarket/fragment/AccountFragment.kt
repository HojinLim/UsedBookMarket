package com.example.usedbookmarket.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.usedbookmarket.*
import com.example.usedbookmarket.model.Friend
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



    companion object{
        private var imageUri : Uri? = null
        private val fireStorage = FirebaseStorage.getInstance().reference
        private val fireDatabase = FirebaseDatabase.getInstance().reference
        private val user = Firebase.auth.currentUser
        private val uid = user?.uid.toString()


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

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if(mContext!= null) mContext= requireContext()


        val auth= FirebaseAuth.getInstance()
        val v: View = inflater.inflate(R.layout.fragment_account,container,false)
        v.findViewById<TextView>(R.id.account_userEmail).text= auth.currentUser?.email

        val photo = v?.findViewById<ImageView>(R.id.account_profile_pic)

        val email = v?.findViewById<TextView>(R.id.account_userEmail)
        val name = v?.findViewById<TextView>(R.id.account_UserName)
        val button = v?.findViewById<Button>(R.id.account_changeName_btn)

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
                    .into(photo!!)
                email?.text = userProfile?.email
                name?.text = userProfile?.name
            }
        })
        //프로필사진 바꾸기
        photo?.setOnClickListener{
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }
        button?.setOnClickListener{
            if(name?.text!!.isNotEmpty()) {
                fireDatabase.child("users/$uid/name").setValue(name.text.toString())
                name.clearFocus()
                Toast.makeText(requireContext(), "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        initIntentActivity(v)

        // 로그아웃 버튼 클릭
        v.findViewById<AppCompatButton>(R.id.account_logout_btn).setOnClickListener {
            if(auth.currentUser != null) auth.signOut()
                Toast.makeText(mContext, "로그아웃 완료",Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), auth.currentUser?.uid.toString(),Toast.LENGTH_SHORT).show()
                val intent= Intent(requireContext(), StartActivity::class.java)
                startActivity(intent)
        }


/*
//TODO 계정 삭제
val auth= FirebaseAuth.getInstance()
val user: FirebaseUser = auth.currentUser!!


val credential = EmailAuthProvider
   .getCredential(user.email!!, user.)
user.reauthenticate(credential)
   .addOnCompleteListener { Log.d("tag", "User re-authenticated.") }


if (user != null) {
   v.findViewById<AppCompatButton>(R.id.account_del_user).setOnClickListener {
       user.delete().addOnCompleteListener(requireActivity()) { task->
           if(task.isSuccessful){
               Toast.makeText(requireContext(), "삭제 완료", Toast.LENGTH_SHORT).show()
               requireActivity().finish()
           }else{
               Toast.makeText(requireContext(), "실패", Toast.LENGTH_SHORT).show()
           }
       }
   }
}
 */


return v
}



    private fun initIntentActivity(v: View){
        v.findViewById<AppCompatButton>(R.id.account_list_books_you_have_btn).setOnClickListener {
            startActivity(Intent(requireContext(), BooksYouHaveActivity::class.java))
        }
        v.findViewById<AppCompatButton>(R.id.account_list_books_you_sell_btn).setOnClickListener {
            startActivity(Intent(requireContext(), BooksYouSellActivity::class.java))
        }
        v.findViewById<AppCompatButton>(R.id.account_list_books_you_like_btn).setOnClickListener {
            startActivity(Intent(requireContext(), BooksYouLikeActivity::class.java))
        }
    }

}