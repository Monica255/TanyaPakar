package com.diklat.tanyapakar.ui.tanyapakar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diklat.tanyapakar.core.data.source.model.Expertise
import com.example.tanyapakar.databinding.ItemFilterSpesifikasiBinding
import com.google.firebase.auth.FirebaseAuth

class AdapterExpertise(
    private val isClickable:Boolean,
    private val onClick: ((Expertise) -> Unit)

) :
    RecyclerView.Adapter<AdapterExpertise.ViewHolder>() {

    var list = mutableListOf<Expertise>()
    //lateinit var ctx: Context
    private val uid= FirebaseAuth.getInstance().currentUser?.uid

    fun submitList(mList: MutableList<Expertise>) {
        list = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        //ctx = parent.context
        val binding =
            ItemFilterSpesifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: ItemFilterSpesifikasiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(expertise: Expertise) {
            binding.root.setOnClickListener {
                onClick(expertise)
            }
            binding.root.isClickable=isClickable
            binding.root.text=(expertise.name)?.capitalize()
        }
    }
}