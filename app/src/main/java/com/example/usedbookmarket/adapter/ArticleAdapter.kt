package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.adapter.ArticleAdapter.ArticleItemViewHolder
import com.example.usedbookmarket.databinding.ItemArticleBinding
import com.example.usedbookmarket.model.ArticleForm

class ArticleAdapter(val clickListener: (ArticleForm) -> Unit): ListAdapter<ArticleForm, ArticleItemViewHolder>(diffUtil) {
    inner class ArticleItemViewHolder(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(bookModel: ArticleForm){
            binding.itemArticleArticleTitleTextView.text= bookModel.title
            binding.itemArticleTime.text= bookModel.description
            binding.itemArticlePriceTextView.text= bookModel.priceSales

            Glide
                .with(binding.itemArticleCoverImageView.context)
                .load(bookModel.coverSmallUrl)
                .into(binding.itemArticleCoverImageView)

            binding.root.setOnClickListener {
                clickListener(bookModel)
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