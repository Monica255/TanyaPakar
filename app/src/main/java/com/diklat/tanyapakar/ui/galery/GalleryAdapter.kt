package com.diklat.tanyapakar.ui.galery

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tanyapakar.databinding.ItemGaleryBinding


class GalleryAdapter(private val onClick: ((String) -> Unit)): RecyclerView.Adapter<GalleryAdapter.ItemViewHolder>() {

    var list= listOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.ItemViewHolder {
        val binding =
            ItemGaleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: GalleryAdapter.ItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount()=list.size

    inner class ItemViewHolder(private val binding: ItemGaleryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(url: String) {
            Glide.with(itemView).load(url).into(binding.imgGalery)
//            binding.imgGalery.setImageURI(url.toUri())
        }
    }

}