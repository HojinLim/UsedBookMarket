package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.adapter.ArticleAdapter.ArticleItemViewHolder
import com.example.usedbookmarket.databinding.ItemArticleBinding
import com.example.usedbookmarket.model.Book

class ArticleAdapter(val clickListener: (Book) -> Unit): ListAdapter<Book, ArticleItemViewHolder>(diffUtil) {
    inner class ArticleItemViewHolder(private val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(bookModel: Book){
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
        val diffUtil= object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}