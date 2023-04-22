package com.ritualkart.sevahetu.utiliy

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.FragmentSuccessBinding

class SuccessFragment : DialogFragment() {

    private var binding : FragmentSuccessBinding? = null
    private var message : String? = ""
    private var image : Int = R.drawable.ic_upload_success

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.ivSuccess.setImageResource(image)
        binding!!.tvMessage.text = message

        binding!!.ivClose.setOnClickListener {
            if(dialog != null && dialog!!.isShowing)
                dismiss()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if(dialog != null && dialog!!.isShowing)
                dismiss()
        }, 2000)
    }


    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    fun setFragmentData(image : Int, message : String){
        this.message = message
        this.image = image
    }
}