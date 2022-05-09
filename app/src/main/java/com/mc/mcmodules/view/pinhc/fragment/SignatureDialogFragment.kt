package com.mc.mcmodules.view.pinhc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.SignaturePadBinding
import com.mc.mcmodules.model.interfaces.OnSignedCaptureListener
import com.mc.mcmodules.view.pinhc.view.SignatureView

class SignatureDialogFragment(private val onSignedListener: OnSignedCaptureListener) :
    DialogFragment(),
    SignatureView.OnSignedListener {

    private lateinit var binding: SignaturePadBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        return initContentView(inflater, container)

    }
    override fun getTheme(): Int {
        return R.style.Dialog_App
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCancel.setOnClickListener { dismiss() }
        binding.buttonClear.setOnClickListener { binding.signatureView.clear() }
        binding. buttonOk.setOnClickListener {
            onSignedListener.onSignatureCaptured(binding.signatureView.getSignatureBitmap(), "")
            dismiss()
        }
        binding.signatureView.setOnSignedListener(this)

        binding.buttonClear.apply {
            postDelayed(400L) {
                performClick()
            }
        }

    }
    override fun onStartSigning() {
    }
    override fun onSigned() {
        binding.buttonOk.isEnabled = true
        binding.buttonClear.isEnabled = true
    }
    override fun onClear() {
        binding.buttonClear.isEnabled = false
        binding.buttonOk.isEnabled = false
    }

    private fun initContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.signature_pad, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

}