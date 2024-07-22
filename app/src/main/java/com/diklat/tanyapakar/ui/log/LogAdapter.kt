package com.diklat.tanyapakar.ui.log

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diklat.tanyapakar.core.data.source.model.Log
import com.diklat.tanyapakar.core.data.source.model.Materi
import com.diklat.tanyapakar.core.util.Helper
import com.diklat.tanyapakar.ui.materi.MateriViewModel
import com.example.tanyapakar.databinding.ItemLogBinding
import com.example.tanyapakar.databinding.ItemMateriBinding


class LogAdapter(
    private val onClick: ((Log) -> Unit),
    private val viewModel: LogViewModel,
    private val context: LifecycleOwner
) : PagingDataAdapter<Log, LogAdapter.ViewHolder>(Companion) {
    inner class ViewHolder(private val binding: ItemLogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: Log) {
            binding.tvFileName.text=data.file_name?.capitalize()
            binding.tvDescription.text=data.description?.capitalize()
            binding.tvDate.text= data.timestamp?.let { Helper.convertTimestampToString(it) }
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
            ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    companion object : DiffUtil.ItemCallback<Log>() {
        override fun areItemsTheSame(oldItem: Log, newItem: Log): Boolean {
            return oldItem.id_log == newItem.id_log
        }
        override fun areContentsTheSame(oldItem: Log, newItem: Log): Boolean {
            return oldItem == newItem
        }
        override fun getChangePayload(oldItem: Log, newItem: Log): Any? = Any()
    }

}