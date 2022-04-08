package com.mc.mcmodules.view.firma

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mc.alternativasur.api.interfaces.ActivityResultHandler
import com.mc.mcmodules.databinding.FragmentFirmaBinding
import com.mc.mcmodules.model.classes.data.DataFirmaResponse
import com.mc.mcmodules.model.classes.library.SharePreference
import com.mc.mcmodules.utils.Utils
import java.lang.Exception

class FirmaFragment :
    Fragment(), ActivityResultHandler {
    //private val TAG: String = this::class.java.simpleName
    private lateinit var binding: FragmentFirmaBinding
    private val preference by lazy { SharePreference.getInstance(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFirmaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding.signatureView) {
            val backGround =  Color.WHITE
            setBackgroundColor(backGround)
            setDrawingColor(Color.parseColor("#9899A6"))

            setLineWidth(20)

            binding.btnClear.setOnClickListener { clear(backGround) }
            binding.btnSave.setOnClickListener {
                try {
                    val path = preference.getStrData("path")
                    saveImage(path)
                    Snackbar.make(this,"Firma guardada",Snackbar.LENGTH_LONG).show()
                    handleResult(DataFirmaResponse(true,"saved path: $path"))
                } catch (e: Exception) {
                    handleResult(DataFirmaResponse(false,e.message.toString()))
                } finally { Utils.freeMemory() }
            }
        }
    }

    override fun onPause() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        super.onPause()
    }

    override fun onResume() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onResume()
    }

    override fun handleResult(parcelable: Parcelable) {
        val intent = Intent()
        intent.putExtra("result_object", parcelable)
        requireActivity().setResult(AppCompatActivity.RESULT_OK, intent)
        requireActivity().finish()
    }

    companion object {
        const val TOUCH_TOLERANCE = 10f
    }
}