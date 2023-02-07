package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.databinding.ItemBooksYouHaveBinding
import com.example.usedbookmarket.model.ArticleForm

class BooksYouHaveListAdapter(val clickListener: (ArticleForm) -> Unit): ListAdapter<ArticleForm, BooksYouHaveListAdapter.BooksYouHaveItemViewHolder>(diffUtil) {
    inner class BooksYouHaveItemViewHolder(private val binding: ItemBooksYouHaveBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(bookModel: ArticleForm){

            Glide
                .with(binding.itemBooksYouHaveCoverImg.context)
                .load(bookModel.coverSmallUrl)
                .into(binding.itemBooksYouHaveCoverImg)

            binding.itemBooksYouHaveBookTitle.text= bookModel.title

            binding.root.setOnClickListener {
                clickListener(bookModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksYouHaveItemViewHolder {
        return BooksYouHaveItemViewHolder(
            ItemBooksYouHaveBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BooksYouHaveItemViewHolder, position: Int) {
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