package com.diklat.tanyapakar.ui.materi

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diklat.tanyapakar.core.data.source.model.Expertise
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.core.data.source.model.Pakar
import com.diklat.tanyapakar.core.util.Helper
import com.diklat.tanyapakar.ui.tanyapakar.ListPakarViewModel
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ItemMateriBinding
import com.example.tanyapakar.databinding.ItemPakarBinding

class MateriAdapter(
    private val onClick: ((Materi) -> Unit),
    private val viewModel: MateriViewModel,
    private val context: LifecycleOwner
) : PagingDataAdapter<Materi, MateriAdapter.ViewHolder>(Companion) {
    inner class ViewHolder(private val binding: ItemMateriBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: Materi) {
            binding.tvTitle.text=data.title?.capitalize()
            binding.tvDescription.text=data.description?.capitalize()
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
            ItemMateriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    companion object : DiffUtil.ItemCallback<Materi>() {
        override fun areItemsTheSame(oldItem: Materi, newItem: Materi): Boolean {
            return oldItem.id_materi == newItem.id_materi
        }
        override fun areContentsTheSame(oldItem: Materi, newItem: Materi): Boolean {
            return oldItem == newItem
        }
        override fun getChangePayload(oldItem: Materi, newItem: Materi): Any? = Any()
    }

}