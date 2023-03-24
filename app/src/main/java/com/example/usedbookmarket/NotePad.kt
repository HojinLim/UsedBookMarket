package com.example.usedbookmarket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NotePad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_pad)

    }
}
        /*


        FirebaseDatabase.getInstance().reference.child("like_list/$uid")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) { }
                override fun onDataChange(snapshot: DataSnapshot) {

                        for (data in snapshot.children) { // snapshot 자식들 사용 가능
                            val item = data.getValue<ArticleForm>()
                            if (item?.aid.equals(aid)) { // 내가 이미 추가한 값이면
                                // TODO 삭제
                                if(!isLiked) // 하트값이 false면
                                    reference.getReference("like_list/$uid/$aid").removeValue()
                                continue

                            } else {
                                if(formModel.liked== false){
                                    heart.background.setTint(resources.getColor(R.color.red))
                                    val changedForm = formModel.copy(liked = true)

                                    reference.getReference("like_list/$uid/$aid")
                                        .setValue(changedForm)

                                    reference.getReference("sell_list")
                                        .child("$aid").setValue(changedForm)
                                    Toast.makeText(
                                        this@CompletedSalesArticleForm,
                                        "\"좋아요!\" 리스트 추가",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }else{
                                    heart.background.setTint(resources.getColor(R.color.white))

                                }
                            }

                        }

                }
            })
    }
}
         */
/*
// friend.clear()
for(data in snapshot.children){ // snapshot 자식들 사용 가능
    val item = data.getValue<ArticleForm>()
    if(item?.aid.equals(formModel.aid)) { // 내가 이미 추가한 값이면
        continue }else{                 //패스

            reference.getReference("like_list/$uid/aid")

            val changedForm = formModel.copy(liked= isLiked)
            reference.getReference("like_list/$uid").push().setValue(changedForm)
            reference.getReference("sell_list").child(changedForm.aid.toString()).setValue(changedForm)
            Toast.makeText(this@CompletedSalesArticleForm, "\"좋아요!\" 리스트 추가", Toast.LENGTH_SHORT).show()
    }

    //friend.add(item!!)
}
//notifyDataSetChanged()
}
})

 */
/*
isLiked = if(!isLiked){
it.background.setTint(Color.RED)

reference.getReference("like_list/$uid/aid")

val changedForm = formModel.copy(isLiked= true)
reference.getReference("like_list/$uid").push().setValue(changedForm)
reference.getReference("sell_list").child(changedForm.aid.toString()).setValue(changedForm)
Toast.makeText(this, "\"좋아요!\" 리스트 추가", Toast.LENGTH_SHORT).show()

true
}else{
it.background.setTint(Color.WHITE)

val changedForm = formModel.copy(isLiked= false)
reference.getReference("like_list/$uid").push().setValue(changedForm)
reference.getReference("sell_list").child(changedForm.aid.toString()).setValue(changedForm)
Toast.makeText(this, "\"좋아요!\" 리스트 해제", Toast.LENGTH_SHORT).show()

false
}

*/