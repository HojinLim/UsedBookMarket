package com.example.usedbookmarket.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.usedbookmarket.R

class CompleteReviewAdapter(private val itemList: List<String>) :
    RecyclerView.Adapter<CompleteReviewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_text_btn, parent, false)
            return ViewHolder(view)
        }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = itemList[position]
            holder.bind(item)
        }

        override fun getItemCount() = itemList.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(item: String) {
                val btn = itemView.findViewById<AppCompatButton>(R.id.item_view_btn)
                btn.text = item



            }
        }
    }
