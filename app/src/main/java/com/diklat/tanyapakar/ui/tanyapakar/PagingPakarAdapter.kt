package com.diklat.tanyapakar.ui.tanyapakar

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diklat.tanyapakar.core.data.source.model.Expertise
import com.diklat.tanyapakar.core.data.source.model.Pakar
import com.diklat.tanyapakar.core.util.Helper
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.ItemPakarBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PagingPakarAdapter(
    private val listExp:List<Expertise>,
    private val onClick: ((Pakar) -> Unit),
    private val onClickChat: ((Pakar,String) -> Unit),
    private val viewModel: ListPakarViewModel,
    private val context: LifecycleOwner,
    private val lifecycleScope:CoroutineScope
) : PagingDataAdapter<Pakar, PagingPakarAdapter.ViewHolder>(Companion) {
    inner class ViewHolder(private val binding: ItemPakarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: Pakar) {
            Glide.with(itemView).load(data.profile_img).placeholder(R.drawable.bg_home).into(binding.imgProfile)

            binding.tvName.text=data.name?.capitalize()
            binding.tvSpec.text=
                data.expertise?.let { Helper.convertExpertiseNamesToString(it,listExp) }?:"-"

            data.id_pakar?.let {idPakar->
                lifecycleScope.launch {
                    val userID=viewModel.getUserIDbyRoleId(idPakar)
                    if(userID==null){
                        binding.btnChat.visibility= View.GONE
                    }else{
                        binding.btnChat.visibility= View.VISIBLE
                        binding.btnChat.setOnClickListener {
                            onClickChat.invoke(data, userID)
                        }
                    }
                }
            }

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
            ItemPakarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    companion object : DiffUtil.ItemCallback<Pakar>() {
        override fun areItemsTheSame(oldItem: Pakar, newItem: Pakar): Boolean {
            return oldItem.id_pakar == newItem.id_pakar
        }
        override fun areContentsTheSame(oldItem: Pakar, newItem: Pakar): Boolean {
            return oldItem == newItem
        }
        override fun getChangePayload(oldItem: Pakar, newItem: Pakar): Any? = Any()
    }

}