@file:Suppress("DEPRECATION")

package com.mc.mcmodules.view.scaninereverso

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.SurfaceHolder
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mc.alternativasur.api.interfaces.ActivityResultHandler
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.ActOcrineReversoBinding
import com.mc.mcmodules.viewmodel.scanine.OCRINEREVERSOViewmodel
import com.mc.mcmodules.viewmodel.scanine.OCRINEREVERSOViewmodel.Companion.FIELD_CIC
import com.mc.mcmodules.viewmodel.scanine.OCRINEREVERSOViewmodel.Companion.FIELD_IDENTIFICADOR_C
import java.io.IOException
import java.util.*


class ActOCRINEREVERSO : AppCompatActivity(), ActivityResultHandler {
    private val TAG = this::class.java.simpleName

    companion object {
        const val CODIGO_INTENT_QR: Int = 101
        const val CODIGO_OK_INE_DATA: Int = 102
    }

    private lateinit var selfViewModel: OCRINEREVERSOViewmodel
    private lateinit var binding: ActOcrineReversoBinding
    private lateinit var sheetBehaviorIne: BottomSheetBehavior<*>
    private lateinit var transition: TransitionDrawable

    private lateinit var mCameraSource: CameraSource
    private var noDataEsMuCo = false

    private lateinit var timer: Timer
    private val noDelay = 12000L

    val listenerbtnGuardar = View.OnClickListener {
        mCameraSource.stop()

        with(binding.lyBottomSheet) {
            val cic = txtCic.text.toString()
            val identificadorC = txtIdentificadorC.text.toString()

            println("CIC lenght:" + cic.length)
            println("CIC : $cic")
            println("IDENTIFICADOR C lenght:" + identificadorC.length)
            println("IDENTIFICADOR C : $identificadorC")

            finish()
        }
    }

    val onclickCic = View.OnClickListener {
        with(binding) {
            with(lyBottomSheet) {
                progressCic.visibility = View.VISIBLE
                txtCic.text = ""
            }
        }
        selfViewModel.updateCampo(FIELD_CIC, "")
        disminurContador()
    }

    val onclickIdentificadorC = View.OnClickListener {
        (it as TextView).text = ""
        with(binding) {
            with(lyBottomSheet) {
                progressIdentificadorC.visibility = View.VISIBLE
                txtIdentificadorC.text = ""
            }
        }

        selfViewModel.updateCampo(FIELD_IDENTIFICADOR_C, "")
        disminurContador()
    }

    val onclickScanCorrect = View.OnClickListener {
        mCameraSource.stop()
        handleResult(selfViewModel.getInfoINE())
    }

    val onclickSetValuesCaptured = View.OnClickListener {
        if (binding.lyBottomSheet.inputCic.visibility == View.VISIBLE){
            selfViewModel.setValueCic(binding.lyBottomSheet.inputCic.editText?.text.toString())
        }
        if (binding.lyBottomSheet.inputIdentificadoC.visibility == View.VISIBLE){
            selfViewModel.setValueIdentificadorC(binding.lyBottomSheet.inputIdentificadoC.editText?.text.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentView()
        initBottomSheet()
        initObservers()
        startCameraSource()
        initCountCamposManual()
    }

    private fun initCountCamposManual() {
        val timerTask = object : TimerTask() {
            override fun run() {
                if (selfViewModel.isEsMuCoScanned()) {
                    runOnUiThread {
                        binding.lyBottomSheet.contentIngresoManual.visibility = View.GONE
                    }
                }
                else {
                    runOnUiThread {
                        binding.lyBottomSheet.contentIngresoManual.visibility = View.VISIBLE
                        sheetBehaviorIne.state = BottomSheetBehavior.STATE_EXPANDED
                        noDataEsMuCo = true
                    }
                }
            }
        }

        timer = Timer()
        timer.schedule(timerTask, noDelay)
    }

    private fun disminurContador() {
        selfViewModel.disminuirContador()
        transition.resetTransition()
        binding.lyBottomSheet.progressScan.visibility = View.VISIBLE
        binding.lyBottomSheet.labelScan.text = getString(R.string.en_progreso)
    }

    private fun startCameraSource() {
        //Create the TextRecognizer
        val textRecognizer = TextRecognizer.Builder(applicationContext).build()

        if (!textRecognizer.isOperational) {
            Toast.makeText(
                this,
                "Tu Celular no soporta reconocimiento de texto",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true) //.setRequestedFps(20.0f)
                .build()

            binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
                @SuppressLint("MissingPermission")
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@ActOCRINEREVERSO, arrayOf(Manifest.permission.CAMERA),
                                1
                            )
                            return
                        }
                        mCameraSource.start(binding.surfaceView.holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) { }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    mCameraSource.stop()
                }
            })


            textRecognizer.setProcessor(object : Detector.Processor<TextBlock?> {
                override fun release() {}

                override fun receiveDetections(detections: Detections<TextBlock?>) {
                    val items = detections.detectedItems
                    if (items.size() != 0) selfViewModel.startThreadOCR(items)
                }
            })
        }
    }

    private fun initContentView() {
        selfViewModel = ViewModelProvider(this)[OCRINEREVERSOViewmodel::class.java]
        binding = ActOcrineReversoBinding.inflate(layoutInflater)
        binding.mainView = this
        binding.lyBottomSheet.mainView = this
        binding.lyBottomSheet.viewModel = selfViewModel
        binding.viewModel = selfViewModel
        binding.lifecycleOwner = this
        transition = binding.lyBottomSheet.headerSheet.background as TransitionDrawable
        val view = binding.root
        setContentView(view)
    }

    private fun initBottomSheet() {
        with(binding) {
            sheetBehaviorIne =
                BottomSheetBehavior.from<RelativeLayout>(lyBottomSheet.bottomSheetIne)
        }
        sheetBehaviorIne.state = BottomSheetBehavior.STATE_COLLAPSED
        sheetBehaviorIne.isHideable = false
    }

    private fun initObservers() {
        selfViewModel.liveCic.observe(this) { cic ->
            with(binding.lyBottomSheet) {
                if (cic.isNotEmpty()) {
                    txtCic.text = cic
                    progressCic.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) inputCic.visibility = View.GONE
                } else {
                    txtCic.text = ""
                    progressCic.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputCic.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }
            }
        }

        selfViewModel.liveIdentificadorC.observe(this) { identificador ->
            with(binding.lyBottomSheet) {
                if (identificador.isNotEmpty()) {
                    txtIdentificadorC.text = identificador
                    progressIdentificadorC.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) inputIdentificadoC.visibility = View.GONE
                } else {
                    txtIdentificadorC.text = ""
                    progressIdentificadorC.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputIdentificadoC.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }
            }
        }

        selfViewModel.liveTotalCampos.observe(this) { contador ->
            println("Contador para la transicion : $contador")
            if (contador == 2) {
                transition.startTransition(500)
                binding.lyBottomSheet.progressScan.visibility = View.GONE
                binding.lyBottomSheet.labelScan.text = getString(R.string.escaneo_listo)
                sheetBehaviorIne.state = BottomSheetBehavior.STATE_EXPANDED
                binding.lyBottomSheet.btnCompararInfo.visibility = View.VISIBLE
                binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.GONE
            } else binding.lyBottomSheet.btnCompararInfo.visibility = View.GONE
        }
    }

    override fun handleResult(parcelable: Parcelable) {
        val intentRegreso = Intent()
        intentRegreso.putExtra("result_object", parcelable)
        setResult(RESULT_OK, intentRegreso)
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        timer.purge()
    }
}