package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.adapter.ArticleAdapter.ArticleItemViewHolder
import com.example.usedbookmarket.databinding.ItemArticleBinding
import com.example.usedbookmarket.model.ArticleForm
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticleAdapter(val clickListener: (ArticleForm) -> Unit): ListAdapter<ArticleForm, ArticleItemViewHolder>(diffUtil) {
    inner class ArticleItemViewHolder(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(articleForm: ArticleForm){
            binding.itemArticleArticleTitleTextView.text= articleForm.title
            binding.itemArticleTime.text= articleForm.description
            binding.itemArticlePriceTextView.text= articleForm.wishPrice

            Glide
                .with(binding.itemArticleCoverImageView.context)
                .load(articleForm.coverSmallUrl)
                .into(binding.itemArticleCoverImageView)

            binding.root.setOnClickListener {
                clickListener(articleForm)
            }
            binding.itemArticleTime.text= articleForm.createdAt

            initBookStatus(articleForm.status)

            // 좋아요 개수 바인딩
//            if(articleForm.likeCount== 0) binding.itemArticleLikeImg.isVisible= false
//            else
            binding.itemArticleLikeText.text= articleForm.likeCount.toString()

            var num= 0

            FirebaseDatabase.getInstance().reference.child("chatrooms")
                .orderByChild("aid").equalTo(articleForm.aid).addValueEventListener(object:
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                            num = snapshot.childrenCount.toInt()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            binding.itemArticleChatMany.text= num.toString()
        }


        private fun initBookStatus(status: String?) {
            when (status) {
                "sale" -> {
                    binding.itemStatus.isVisible = false
                    binding.itemStatus2.isVisible = false
                }
                "reserve" -> {
                    binding.itemStatus.isVisible = true
                    binding.itemStatus2.isVisible = false
                }
                "sold" -> {
                    binding.itemStatus.isVisible = false
                    binding.itemStatus2.isVisible = true
                }

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleItemViewHolder {
        return ArticleItemViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ArticleItemViewHolder, position: Int) {
        holder.bind(currentList[position])
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