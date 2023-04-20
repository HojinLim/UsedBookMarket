package com.example.usedbookmarket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.databinding.ItemBooksYouSellBinding
import com.example.usedbookmarket.model.ArticleForm
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class BooksYouSellActivity: AppCompatActivity() {
    private lateinit var articleDB: DatabaseReference
    private lateinit var adapter: BooksYouSellListAdapter
    private lateinit var recyclerView: RecyclerView
    private val articleFormList= mutableListOf<ArticleForm>()
    private var auth= Firebase.auth

    @SuppressLint("MissingInflatedId")
    private val listener= object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleForm: ArticleForm? = snapshot.getValue(ArticleForm::class.java)
            articleForm ?: return
            if(articleForm.uid == auth.currentUser?.uid){
                articleFormList.add(articleForm)
            }
            adapter.submitList(articleFormList)
            adapter.notifyDataSetChanged()
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val articleForm: ArticleForm? = snapshot.getValue(ArticleForm::class.java)
            articleForm ?: return


            adapter.submitList(articleFormList)
            adapter.notifyDataSetChanged()
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) { }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books_you_sell)

        //TODO 해당 계정 내에 소지 중인 책 데이터 가져오기

        recyclerView = findViewById(R.id.books_you_sell_recyclerView)


        adapter= BooksYouSellListAdapter {  }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        articleDB= Firebase.database.reference.child("sell_list")

        articleDB.addChildEventListener(listener)

        adapter.submitList(articleFormList)

        // 뒤로가기 버튼
        findViewById<AppCompatButton>(R.id.books_you_have_backButton).setOnClickListener {
            onBackPressed()
        }


    }
    inner class BooksYouSellListAdapter(val clickListener: (ArticleForm) -> Unit): ListAdapter<ArticleForm, BooksYouSellListAdapter.BooksYouSellItemViewHolder>(diffUtil) {
        inner class BooksYouSellItemViewHolder(private val binding: ItemBooksYouSellBinding): RecyclerView.ViewHolder(binding.root){

            fun bind(articleModel: ArticleForm){

                Glide
                    .with(binding.itemBooksYouSellCoverImg.context)
                    .load(articleModel.coverSmallUrl)
                    .into(binding.itemBooksYouSellCoverImg)

                binding.itemBooksYouSellBookTitle.text= articleModel.title

                binding.root.setOnClickListener {
                    clickListener(articleModel)
                }

                // 거래 예약 버튼
                binding.itemBookReserveBtn.setOnClickListener {
                    initBookStatus("reserve", articleModel)
                }
                // 거래 완료 버튼
                binding.itemBookSoldBtn.setOnClickListener {
                    initBookStatus("sold", articleModel)
                }
                // 거래중 버튼
                binding.itemBookSellBtn.setOnClickListener {
                    initBookStatus("sale", articleModel)
                }

                // 수정하기 버튼
                binding.itemBooksYouSellEditBtn.setOnClickListener {
                    val intent = Intent(this@BooksYouSellActivity, SalesArticleFormActivity::class.java)
                    intent.putExtra("formModel", articleModel)
                    intent.putExtra("flag", "B")
                    startActivity(intent)
                }

                binding.itemBooksYouSellCoverImg.setOnClickListener {// 이미지 확대
                    val intent= Intent(this@BooksYouSellActivity, ZoomImageActivity::class.java)
                    intent.putExtra("formImage", articleModel.coverSmallUrl)
                    startActivity(intent)
                }
            }

            private fun initBookStatus(status: String?, articleModel: ArticleForm) {

                when(status){
                    "sale"->{
                        binding.itemBookStatus.isVisible= false
                        binding.itemBookStatus2.isVisible= false
                    }
                    "reserve"->{
                        binding.itemBookStatus.isVisible= true
                        binding.itemBookStatus2.isVisible= false
                    }
                    "sold"->{
                        binding.itemBookStatus.isVisible= false
                        binding.itemBookStatus2.isVisible= true
                    }

                }
                val aid= articleModel.aid
                val cformModel= articleModel.copy(status="$status")
                FirebaseDatabase.getInstance().reference
                    .child("sell_list/$aid/")
                    .setValue(cformModel)
                //adapter.notifyDataSetChanged()

                val handler = Handler()

                val r = Runnable { adapter.notifyDataSetChanged() }

                handler.post(r)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksYouSellItemViewHolder {
            return BooksYouSellItemViewHolder(
                ItemBooksYouSellBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )


        }

        override fun onBindViewHolder(holder: BooksYouSellListAdapter.BooksYouSellItemViewHolder, position: Int) {
            holder.bind(currentList[position])
        }


    }
    companion object {
        val diffUtil= object : DiffUtil.ItemCallback<ArticleForm>() {
            override fun areItemsTheSame(oldItem: ArticleForm, newItem: ArticleForm): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ArticleForm, newItem: ArticleForm): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}