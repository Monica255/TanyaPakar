package com.diklat.tanyapakar.ui.chat


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.diklat.tanyapakar.core.data.source.model.ChatMessage
import com.example.tanyapakar.databinding.ViewChatBinding

class BubbleChatView : RelativeLayout {
//    private var onThreadClickListener: ((String) -> Unit)? = null
//
//    fun setOnThreadClickListener(listener: (String) -> Unit) {
//        onThreadClickListener = listener
//        binding.parent.setOnClickListener {
//            val x= (binding.parent.tag as? String) ?: return@setOnClickListener
//            onThreadClickListener?.invoke(x)
//            Log.d("chaterror","chat clicked "+x)
//        }
//
//    }


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        gravity = Gravity.CENTER
    }
    lateinit var binding: ViewChatBinding
    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = ViewChatBinding.inflate(inflater, this, true)
    }

    fun setData(data: ChatMessage,uid:String) {
//        binding.parent.tag = data.id
        Log.d("chatview",data.toString())
        Log.d("chatview",uid.toString())
        if (data.sentBy == uid) {
            binding.tvSend.text = data.message
            binding.llChatSend.visibility=View.VISIBLE
            binding.llReceive.visibility=View.GONE
        } else {
            binding.tvReceive.text = data.message
            binding.llChatSend.visibility=View.GONE
            binding.llReceive.visibility=View.VISIBLE
        }
    }
}