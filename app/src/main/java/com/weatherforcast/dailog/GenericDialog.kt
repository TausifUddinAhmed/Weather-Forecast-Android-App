package com.weatherforcast.dailog

import android.app.AlertDialog
import android.app.Dialog
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AppCompatDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.weatherforcast.databinding.DialogueSuccessBinding

@AndroidEntryPoint
class GenericDialog (onGenericDialogButtonClickListener: OnGenericDialogButtonClickListener): AppCompatDialogFragment() {
    private var binding: DialogueSuccessBinding? = null

    private  var mOnSuccessDialogButtonClickListener = onGenericDialogButtonClickListener

    private  var title : String = ""
    private  var suTitle : String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogueSuccessBinding.inflate(
            LayoutInflater.from(
                context
            )
        )
        val builder = AlertDialog.Builder(context)
        builder.setView(binding!!.root)

        try {
            title = requireArguments().getString("title").toString()
            suTitle = requireArguments().getString("subTitle").toString()

            if(title.isNotEmpty()) {
                binding?.textViewTitle?.text = title
            }else{
                binding?.textViewTitle?.visibility = View.GONE

            }
            binding?.textViewSubTitle?.text =  suTitle

        }catch ( e: Exception){


        }

        binding!!.buttonOkay.setOnClickListener {


            mOnSuccessDialogButtonClickListener.onOkButtonClick()

        }
        
        return builder.create()
    }
}