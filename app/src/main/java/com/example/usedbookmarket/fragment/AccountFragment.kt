package com.example.usedbookmarket.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.usedbookmarket.BooksYouHaveActivity
import com.example.usedbookmarket.BooksYouLikeActivity
import com.example.usedbookmarket.BooksYouSellActivity
import com.example.usedbookmarket.R


class AccountFragment : Fragment(R.layout.fragment_account) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v: View = inflater.inflate(R.layout.fragment_account,container,false)

        initIntentActivity(v)

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