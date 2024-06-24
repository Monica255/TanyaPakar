package com.diklat.tanyapakar.ui.tanyapakar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.diklat.tanyapakar.core.data.source.model.Expertise
import com.example.tanyapakar.databinding.FragmentExpertiseBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ExpertiseFragment : DialogFragment() {

    private var _binding: FragmentExpertiseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListPakarViewModel by activityViewModels()
    private lateinit var onGetData: OnGetData
    private lateinit var adapter: AdapterExpertise
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onGetData = context as OnGetData
        } catch (e: ClassCastException) {
            Log.e(
                "TAG", "onAttach: ClassCastException: "
                        + e.message
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        val layoutManagerCommonTopic = FlexboxLayoutManager(requireActivity())
        layoutManagerCommonTopic.flexDirection = FlexDirection.ROW
        binding.rvSpesifikasi.layoutManager = layoutManagerCommonTopic

        binding.btnAllSpec.setOnClickListener {
            onGetData.handleData(null)
            dismiss()
        }

        adapter = AdapterExpertise(true) { topic ->
            onGetData.handleData(topic)
            dismiss()
        }


        binding.rvSpesifikasi.adapter = adapter

        viewModel.expertise.observe(requireActivity()){
            if (!it.isEmpty()){
                adapter.submitList(it)
            }else{
                binding.tvLabelCommonTopic.visibility=View.GONE
            }
        }

    }
    override fun onStart() {
        super.onStart()
        dialog?.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpertiseBinding.inflate(inflater, container, false) // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}