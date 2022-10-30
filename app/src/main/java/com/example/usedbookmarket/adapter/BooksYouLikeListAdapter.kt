package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.adapter.BooksYouLikeListAdapter.BooksYouLikeItemViewHolder
import com.example.usedbookmarket.databinding.ItemBooksYouLikeBinding
import com.example.usedbookmarket.model.Book

class BooksYouLikeListAdapter(val clickListener: (Book) -> Unit): ListAdapter<Book, BooksYouLikeItemViewHolder>(diffUtil) {
    inner class BooksYouLikeItemViewHolder(private val binding: ItemBooksYouLikeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(bookModel: Book){

            Glide
                .with(binding.itemBooksYouLikeCoverImageView.context)
                .load(bookModel.coverSmallUrl)
                .into(binding.itemBooksYouLikeCoverImageView)

            binding.itemBooksYouLikeArticleTitleTextView.text= bookModel.title

            binding.root.setOnClickListener {
                clickListener(bookModel)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksYouLikeItemViewHolder {
        return BooksYouLikeItemViewHolder(
            ItemBooksYouLikeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BooksYouLikeListAdapter.BooksYouLikeItemViewHolder, position: Int) {
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