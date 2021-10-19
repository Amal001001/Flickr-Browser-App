package com.example.flickrbrowserapp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ItemRowBinding

class RecyclerViewAdapter(val activity:MainActivity, private val photos: ArrayList<Image>): RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        Log.d("main", ".onCreateViewHolder new view requested")
        return ItemViewHolder(
            ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = photos[position]

        holder.binding.apply {
            Glide.with(activity).load(photo.link).into(imageView)
            textView.text = photo.title
            itemRowLayout.setOnClickListener { activity.bigPhoto(photo.link) }
        }
    }
    override fun getItemCount() = photos.size
}