package com.diklat.tanyapakar.ui.galery

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.tanyapakar.R
import com.example.tanyapakar.databinding.FragmentGalleryBinding
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment


private const val ARG_PARAM1 = "param1"

class GalleryFragment : SupportBlurDialogFragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private var param1: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        param1?.let {
            Glide.with(binding.root).load(it).placeholder(R.drawable.icon_camera).into(binding.imgGalery)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryBinding.inflate(
            inflater,
            container,
            false
        ) // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        val lp = dialog!!.window!!.attributes
        lp.dimAmount = 0.5f
        dialog!!.window!!.attributes = lp
    }

    override fun isDimmingEnable(): Boolean {
        return true
    }

    override fun getBlurRadius(): Int {
        return 9
    }
    override fun onStart() {
        super.onStart()

        dialog?.window
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            GalleryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
