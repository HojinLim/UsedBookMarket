package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.adapter.BooksYouSellListAdapter.BooksYouSellItemViewHolder
import com.example.usedbookmarket.databinding.ItemBooksYouSellBinding
import com.example.usedbookmarket.model.Book

class BooksYouSellListAdapter(val clickListener: (Book) -> Unit): ListAdapter<Book, BooksYouSellItemViewHolder>(diffUtil) {
    inner class BooksYouSellItemViewHolder(private val binding: ItemBooksYouSellBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(bookModel: Book){

            Glide
                .with(binding.itemBooksYouSellCoverImg.context)
                .load(bookModel.coverSmallUrl)
                .into(binding.itemBooksYouSellCoverImg)

            binding.itemBooksYouSellBookTitle.text= bookModel.title

            binding.root.setOnClickListener {
                clickListener(bookModel)
            }
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