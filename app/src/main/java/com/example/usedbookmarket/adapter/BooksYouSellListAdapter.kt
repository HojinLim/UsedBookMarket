package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.usedbookmarket.adapter.BooksYouSellListAdapter.BooksYouSellItemViewHolder
import com.example.usedbookmarket.databinding.ItemBooksYouSellBinding
import com.example.usedbookmarket.model.ArticleForm


class BooksYouSellListAdapter(val clickListener: (ArticleForm) -> Unit): ListAdapter<ArticleForm, BooksYouSellItemViewHolder>(diffUtil) {
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

            binding.itemBooksYouSellEditBtn.setOnClickListener {

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