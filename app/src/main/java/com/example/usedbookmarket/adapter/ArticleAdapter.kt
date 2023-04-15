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

class ArticleAdapter(val clickListener: (ArticleForm) -> Unit): ListAdapter<ArticleForm, ArticleItemViewHolder>(diffUtil) {
    inner class ArticleItemViewHolder(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(articleForm: ArticleForm){
            binding.itemArticleArticleTitleTextView.text= articleForm.title
            binding.itemArticleTime.text= articleForm.description
            binding.itemArticlePriceTextView.text= articleForm.priceSales

            Glide
                .with(binding.itemArticleCoverImageView.context)
                .load(articleForm.coverSmallUrl)
                .into(binding.itemArticleCoverImageView)

            binding.root.setOnClickListener {
                clickListener(articleForm)
            }
            binding.itemArticleTime.text= articleForm.createdAt


            initBookStatus(articleForm.status)
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