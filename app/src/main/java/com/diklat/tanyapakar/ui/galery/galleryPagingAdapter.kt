package com.diklat.tanyapakar.ui.galery

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.ui.materi.MateriViewModel
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ItemGaleryBinding
import com.example.tanyapakar.databinding.ItemMateriBinding


class GalleryPagingAdapter(
    private val onClick: ((String) -> Unit)
) : PagingDataAdapter<String, GalleryPagingAdapter.ViewHolder>(Companion) {
    inner class ViewHolder(private val binding: ItemGaleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: String) {
            Glide.with(itemView).load(data).placeholder(R.drawable.bg_home).into(binding.imgGalery)
            binding.root.setOnClickListener {
                onClick.invoke(data)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemGaleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    companion object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem== newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        override fun getChangePayload(oldItem: String, newItem: String): Any? = Any()
    }

}