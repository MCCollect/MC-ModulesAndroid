@file:Suppress("DEPRECATION")

package com.mc.mcmodules.view.scanine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mc.alternativasur.api.interfaces.ActivityResultHandler
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.ActOcrineBinding
import com.mc.mcmodules.model.classes.data.*
import com.mc.mcmodules.utils.Utils
import com.mc.mcmodules.viewmodel.scanine.OCRINEViewmodel
import com.tapadoo.alerter.Alerter
import java.io.IOException
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL
import java.util.*
import java.util.regex.Pattern


class ActOCRINE : AppCompatActivity(), ActivityResultHandler {

    companion object {
        const val CODIGO_INTENT_QR: Int = 100
        const val CODIGO_OK_INE_DATA: Int = 101
    }

    private val TAG = this::class.java.simpleName

    private lateinit var selfViewModel: OCRINEViewmodel
    private lateinit var binding: ActOcrineBinding
    private lateinit var sheetBehaviorIne: BottomSheetBehavior<*>
    private lateinit var transition: TransitionDrawable


    private lateinit var mCameraSource: CameraSource


    private var flagCodigo = true
    private var noDataEsMuCo = false
    private var intEmision = 0
    private var typeOCR = 0

    private lateinit var timer: Timer
    private val noDelay = 12000L


    val listenerImgIne = View.OnClickListener {
        when (intEmision) {
            0 -> {
                println("aun no se tiene el dato de tipo")
                Utils.hideKeyboard(this@ActOCRINE)
                ContextCompat.getDrawable(this@ActOCRINE, R.drawable.ic_warning)?.let { it1 ->
                    Alerter.create(this@ActOCRINE)
                        .setTitle("No se puede capturar datos del reverso")
                        .setText("Escaneo todos los campos por favor")
                        .setIcon(it1)
                        .setBackgroundColorRes(R.color.error)
                        .setDuration(2500)
                        .show()
                }
            }
            in 1..2012 -> {
                flagCodigo = false

                with(binding) {
                    with(lyBottomSheet) {
                        etOCR.visibility = View.VISIBLE
                        editTextOCR.requestFocus()
                    }
                    imgIne.setImageResource(R.drawable.ic_ine_atras)
                }

            }
            else -> {
                try {
                    mCameraSource.stop()
//                    val intent = Intent(this@ActOCRINEREVERSO, ActScanCodes::class.java)
//                    startActivityForResult(intent, CODIGO_INTENT_QR)
                    val intent = Intent(this, ActScanCodes::class.java)
                    startActivityForResult(intent, ActScanCodes.CODIGO_OK_QR)


                } catch (e: Exception) {
                    Log.e(getString(R.string.app_name), "failed to open Camera")
                    e.printStackTrace()
                } finally {
                    Utils.freeMemory()
                }
            }
        }

    }

    val listenerbtnGuardar = View.OnClickListener {


        mCameraSource.stop()

        with(binding.lyBottomSheet) {

            //InformacionSolicitante.generarCurp = false


            val patternCP = Pattern.compile(".\\s*(\\d{5})\\s*.")
            val matcherCP =
                patternCP.matcher(txtDomicilio.text.toString().uppercase(Locale.getDefault()))

            val nombreCompleto: Array<String> = txtNombre.text.toString().trim { it <= ' ' }
                .split(" ").toTypedArray()
            println("Nommbre lenght:" + nombreCompleto.size)


            println("Fecha Nacimiento : ${txtFecha.text}")


            if (nombreCompleto.size >= 3) {
                var nombreText = ""
                for (i in 2 until nombreCompleto.size) {
                    nombreText = nombreText + " " + nombreCompleto[i]
                }

                println("Nombre : ${nombreText.trim { it <= ' ' }}")
                println("Paterno : ${nombreCompleto[0]}")
                println("Materno : ${nombreCompleto[1]}")


            } else {
                println("Nombre : ")
                println("Paterno : ")
                println("Materno : ")
            }


            try {
                if (matcherCP.find()) {
                    println("CP : ${matcherCP.group(1)?.uppercase(Locale.getDefault())}")
                } else {
                    println("CP : ")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //System.out.println("Este es el estado que se esta buscando : " + curp.getText().toString().substring(11, 13));


            //System.out.println("Este es el estado que se esta buscando : " + curp.getText().toString().substring(11, 13));
            if (txtSexo.text.toString() == "H") {
                println("Sexo : ${txtSexo.text}")
            } else {
                println("Sexo : ${txtSexo.text}")
            }

            if (txtCurp.text.toString().length >= 15) {

                println("CURP : ${txtCurp.text}")
                println("RFC : ${txtCurp.text.toString().substring(0, 10)}")

                try {


                    println("Nacimiento 1: ${txtCurp.text.toString().substring(11, 13)} ")

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }


            if (txtFecha.text.toString().length == 10) {
                try {
                    val year: Int = txtFecha.text.toString().substring(6, 10).toInt()
                    val month: Int = txtFecha.text.toString().substring(3, 5).toInt()
                    val day: Int = txtFecha.text.toString().substring(0, 2).toInt()
                    if (year in 1901..2017 && month > 0 && month < 13 && day > 0 && day < 32) {
                        val newDate = Calendar.getInstance()
                        var age = newDate[Calendar.YEAR] - year
                        /**
                         * Si el mes en el que estamos es menor al mes que inserto,
                         * aun no cuemple años y se le resta 1 año
                         */
                        if (newDate[Calendar.MONTH] + 1 < month) age-- else if (newDate[Calendar.MONTH] + 1 == month) if (day < newDate[Calendar.DAY_OF_MONTH]) age--
                        println("Edad : $age")

                    }
                } catch (exeption: Exception) {
                    //Toast toast = Toast.makeText(getContext(), "Revise fecha", Toast.LENGTH_SHORT);
                    //toast.show();
                    exeption.printStackTrace()
                } finally {
                    Utils.freeMemory()
                }
            }

            try {
                //solo si es ine

                println("Clave lector : ${txtClaveElector.text}")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


            finish()

        }

    }

    val listenerbtnValidar = View.OnClickListener {

        with(binding.lyBottomSheet) {
            when (intEmision) {
                0 -> {
                    println("aun no se tiene el dato de tipo")
                }
                in 1..2012 -> {
                    val intent = Intent(this@ActOCRINE, ActWebView::class.java)
                    intent.putExtra("flag", "1")
                    //codUni
                    if (typeOCR == 1) intent.putExtra(
                        "codOCR",
                        txtCodUni.text.toString()
                    ) else if (typeOCR == 2) intent.putExtra("codOCR", editTextOCR.text.toString())
                    intent.putExtra("emision", txtEmision.text.toString())
                    intent.putExtra("claveElector", txtClaveElector.text.toString())
                    startActivity(intent)
                }
                else -> {
                    println("soy ultimo modelo 1")
                    val intent = Intent(this@ActOCRINE, ActWebView::class.java)
                    intent.putExtra("flag", "2")
                    intent.putExtra("url", txtCodQR.text.toString())
                    startActivity(intent)
                }
            }
        }


    }

    val listenerCodQr = View.OnClickListener {

    }

    val onclickNombre = View.OnClickListener {

        with(binding) {
            with(lyBottomSheet) {
                progressNombre.visibility = View.VISIBLE
                txtNombre.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_NOMBRE, "")
        disminurContador()


    }

    val onclickDomicilio = View.OnClickListener {

        (it as TextView).text = ""
        with(binding) {
            with(lyBottomSheet) {
                progressDomicilio.visibility = View.VISIBLE
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_DOMICILIO, "")
        disminurContador()


    }

    val onclickFecha = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressFecha.visibility = View.VISIBLE
                txtFecha.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_FECHA, "")
        disminurContador()
        disminurContador()

    }

    val onclickSexo = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressSexo.visibility = View.VISIBLE
                txtSexo.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_SEXO, "")
        disminurContador()

    }

    val onclickclaveElector = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressClaveElector.visibility = View.VISIBLE
                txtClaveElector.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_CLAVE_ELECTOR, "")
        disminurContador()

    }

    val onclickCurp = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressCurp.visibility = View.VISIBLE
                txtCurp.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_CURP, "")
        disminurContador()

    }


    val onclickEmision = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressEmision.visibility = View.VISIBLE
                txtEmision.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_EMISION, "")
        disminurContador()

    }

    val onclickVigencia = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressVigencia.visibility = View.VISIBLE
                txtVigencia.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_VIGENCIA, "")
        disminurContador()

    }


    val onclickTipoIne = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressTipo.visibility = View.VISIBLE
                txtTipo.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_TIPO_INE, "")
        disminurContador()

    }

    val onclickEstado = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressEstado.visibility = View.VISIBLE
                txtEstado.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_ESTADO, "")
        disminurContador()

    }

    val onclickMunicipio = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressMunicipio.visibility = View.VISIBLE
                txtMunicipio.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_MUNICIPIO, "")
        disminurContador()

    }

    val onclickColonia = View.OnClickListener {


        with(binding) {

            with(lyBottomSheet) {
                progressLocalidad.visibility = View.VISIBLE
                txtLocalidad.text = ""
            }
        }

        selfViewModel.updateCampo(OCRINEViewmodel.FIELD_COLONIA, "")
        disminurContador()

    }

    val onclickScanCorrect = View.OnClickListener {
        mCameraSource.stop()
        handleResult(selfViewModel.getInfoINE())
    }

    val onclickSetValuesCaptured = View.OnClickListener {

        if (binding.lyBottomSheet.inputNOMBRE.visibility == View.VISIBLE){
            selfViewModel.setValueNOMBRE(binding.lyBottomSheet.inputNOMBRE.editText?.text.toString())
        }
        if (binding.lyBottomSheet.inputDOMICILO.visibility == View.VISIBLE){
            selfViewModel.setValueDOMICILO(binding.lyBottomSheet.inputDOMICILO.editText?.text.toString())
        }
        if (binding.lyBottomSheet.inputFechaNac.visibility == View.VISIBLE){
            selfViewModel.setValueFechaNac(binding.lyBottomSheet.inputFechaNac.editText?.text.toString())
        }
        if (binding.lyBottomSheet.inputEDAD.visibility == View.VISIBLE){
            selfViewModel.setValueEDAD(binding.lyBottomSheet.inputEDAD.editText?.text.toString())
        }
        if (binding.lyBottomSheet.inputSEXO.visibility == View.VISIBLE){
            selfViewModel.setValueSEXO(binding.lyBottomSheet.inputSEXO.editText?.text.toString())
        }
        if (binding.lyBottomSheet.inputCLAVELECTOR.visibility == View.VISIBLE){
            selfViewModel.setValueCLAVELECTOR(binding.lyBottomSheet.inputCLAVELECTOR.editText?.text.toString())
        }
        if (binding.lyBottomSheet.inputCURP.visibility == View.VISIBLE){
            selfViewModel.setValueCURP(binding.lyBottomSheet.inputCURP.editText?.text.toString())
        }





        if(binding.lyBottomSheet.inputEstado.visibility == View.VISIBLE){
            selfViewModel.setValueEstado(binding.lyBottomSheet.txtCapEstado.text.toString())

        }

        if(binding.lyBottomSheet.inputMunicipio.visibility == View.VISIBLE){
            selfViewModel.setValueMunicipio(binding.lyBottomSheet.txtCapMunicipio.text.toString())

        }


        if(binding.lyBottomSheet.inputColonia.visibility == View.VISIBLE){
            selfViewModel.setValueColonia(binding.lyBottomSheet.txtCapColonia.text.toString())

        }

        if(binding.lyBottomSheet.inputEmision.visibility == View.VISIBLE){
            selfViewModel.setValueEmision(binding.lyBottomSheet.txtCapEmision.text.toString())

        }

        if(binding.lyBottomSheet.inputVigencia.visibility == View.VISIBLE){
            selfViewModel.setValueVigencia(binding.lyBottomSheet.txtCapVigencia.text.toString())

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentView()
        initObjects()
        //initViews()
        initBottomSheet()
        setOCR_Automatico()
        setOCR_Manual()
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

            /*mCameraView.setOnTouchListener { v, event ->
                cameraFocus(
                    event,
                    mCameraSource,
                    Camera.Parameters.FOCUS_MODE_AUTO
                )
                false
            }*/

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
                                this@ActOCRINE, arrayOf(Manifest.permission.CAMERA),
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
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    mCameraSource.stop()
                }
            })


            textRecognizer.setProcessor(object : Detector.Processor<TextBlock?> {
                override fun release() {}

                override fun receiveDetections(detections: Detections<TextBlock?>) {
                    val items = detections.detectedItems
                    if (items.size() != 0) {
                        selfViewModel.startThreadOCR(items)

                    }
                }
            })


        }
    }


    private fun initContentView() {
        selfViewModel = ViewModelProvider(this).get(OCRINEViewmodel::class.java)
        binding = ActOcrineBinding.inflate(layoutInflater)
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

    /*private fun initViews() {
        with(binding) {


        }
    }*/

    private fun initObjects() {


    }

    private fun initObservers() {


        selfViewModel.liveNombre.observe(this) { nombre ->
            with(binding.lyBottomSheet) {
                if (nombre.isNotEmpty()) {
                    txtNombre.text = nombre
                    progressNombre.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) {
                        inputNOMBRE.visibility = View.GONE
                    }

                }
                else {
                    txtNombre.text = ""
                    progressNombre.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputNOMBRE.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }
            }
        }

        selfViewModel.liveDomicilio.observe(this) { domicilio ->

            with(binding.lyBottomSheet) {


                if (domicilio.isNotEmpty()) {
                    txtDomicilio.text = domicilio
                    progressDomicilio.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) {
                        inputDOMICILO.visibility = View.GONE
                    }

                } else {
                    txtDomicilio.text = ""
                    progressDomicilio.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputDOMICILO.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }

            }


        }

        selfViewModel.liveFecha.observe(this) { fecha ->

            with(binding.lyBottomSheet) {


                if (fecha.isNotEmpty()) {
                    txtFecha.text = fecha
                    progressFecha.visibility = View.GONE
                    progressEdad.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) {
                        inputFechaNac.visibility = View.GONE
                        inputEDAD.visibility = View.GONE
                    }

                } else {
                    txtFecha.text = ""
                    txtEdad.text = ""
                    progressFecha.visibility = View.VISIBLE
                    progressEdad.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputFechaNac.visibility = View.VISIBLE
                        inputEDAD.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }

            }


        }

        selfViewModel.livesexo.observe(this) { sexo ->

            with(binding.lyBottomSheet) {


                if (sexo.isNotEmpty()) {
                    txtSexo.text = sexo
                    progressSexo.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) {
                        inputSEXO.visibility = View.GONE
                    }
                } else {
                    txtSexo.text = ""
                    progressSexo.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputSEXO.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }

            }


        }

        selfViewModel.liveClaveLector.observe(this) { claveElector ->

            with(binding.lyBottomSheet) {


                if (claveElector.isNotEmpty()) {
                    txtClaveElector.text = claveElector
                    progressClaveElector.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) {
                        inputCLAVELECTOR.visibility = View.GONE
                    }

                } else {
                    txtClaveElector.text = ""
                    progressClaveElector.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputCLAVELECTOR.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }

            }


        }

        selfViewModel.liveCurp.observe(this) { curp ->

            with(binding.lyBottomSheet) {


                if (curp.isNotEmpty()) {
                    txtCurp.text = curp
                    progressCurp.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) {
                        inputCURP.visibility = View.GONE
                    }
                } else {
                    txtCurp.text = ""
                    progressCurp.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputCURP.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }

            }


        }

        selfViewModel.liveEmision.observe(this) { emision ->

            with(binding.lyBottomSheet) {


                if (emision.isNotEmpty()) {
                    txtEmision.text = emision
                    progressEmision.visibility = View.GONE
                    selfViewModel.aumentarContador()

                    intEmision = try {
                        emision.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        0
                    }

                    if (noDataEsMuCo) {

                        inputEmision.visibility = View.GONE
                    }


                } else {
                    txtEmision.text = ""
                    progressEmision.visibility = View.VISIBLE

                    if (noDataEsMuCo) {
                        inputEmision.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }

                }

            }


        }


        selfViewModel.liveVigencia.observe(this) { vigencia ->

            with(binding.lyBottomSheet) {


                if (vigencia.isNotEmpty()) {
                    txtVigencia.text = vigencia
                    progressVigencia.visibility = View.GONE
                    selfViewModel.aumentarContador()


                    if (noDataEsMuCo) {

                        inputVigencia.visibility = View.GONE
                    }


                } else {
                    txtVigencia.text = ""
                    progressVigencia.visibility = View.VISIBLE

                    if (noDataEsMuCo) {
                        inputVigencia.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }

                }

            }


        }

/*
        selfViewModel.liveTipoIne.observe(this, { tipoIne ->

            with(binding.lyBottomSheet) {


                if (tipoIne.isNotEmpty()) {
                    txtTipo.text = tipoIne
                    progressTipo.visibility = View.GONE
                    selfViewModel.aumentarContador()

                } else {
                    txtTipo.text = ""
                    progressTipo.visibility = View.VISIBLE

                }

            }


        })*/

        selfViewModel.liveEstado.observe(this) { estado ->
            with(binding.lyBottomSheet) {
                if (estado.isNotEmpty()) {
                    txtEstado.text = estado
                    progressEstado.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) {
                        inputEstado.visibility = View.GONE
                    }
                }
                else {
                    txtEstado.text = ""
                    progressEstado.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputEstado.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }
            }
        }


        selfViewModel.liveMunicipio.observe(this) { municipio ->
            with(binding.lyBottomSheet) {
                if (municipio.isNotEmpty()) {
                    txtMunicipio.text = municipio
                    progressMunicipio.visibility = View.GONE
                    selfViewModel.aumentarContador()
                    if (noDataEsMuCo) {
                        inputMunicipio.visibility = View.GONE
                    }
                }
                else {
                    txtMunicipio.text = ""
                    progressMunicipio.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputMunicipio.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }
                }
            }
        }

        selfViewModel.liveColonia.observe(this) { localidad ->

            with(binding.lyBottomSheet) {


                if (localidad.isNotEmpty()) {
                    txtLocalidad.text = localidad
                    progressLocalidad.visibility = View.GONE
                    selfViewModel.aumentarContador()

                    if (noDataEsMuCo) {
                        inputColonia.visibility = View.GONE
                    }


                } else {
                    txtLocalidad.text = ""
                    progressLocalidad.visibility = View.VISIBLE
                    if (noDataEsMuCo) {
                        inputColonia.visibility = View.VISIBLE
                        binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.VISIBLE
                    }

                }

            }


        }



        selfViewModel.liveTotalCampos.observe(this) { contador ->


            println("Contador para la transicion : $contador")
            if (contador == 12) {

                transition.startTransition(500)
                binding.lyBottomSheet.progressScan.visibility = View.GONE
                binding.lyBottomSheet.labelScan.text = getString(R.string.escaneo_listo)
                sheetBehaviorIne.state = BottomSheetBehavior.STATE_EXPANDED
                binding.lyBottomSheet.btnCompararInfo.visibility = View.VISIBLE
                binding.lyBottomSheet.btnSetValuesCaptured.visibility = View.GONE

            } else {
                binding.lyBottomSheet.btnCompararInfo.visibility = View.GONE
            }

        }


    }

    private fun setOCR_Automatico() {

        /*
        codUni.setVisibility(View.VISIBLE);
                    progressOCR.setVisibility(View.VISIBLE);
                    ttCodUni.setVisibility(View.VISIBLE);
         */

        with(binding.lyBottomSheet) {
            txtCodUni.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (charSequence.length == 13) {
                        println(charSequence)
                        typeOCR = 1
                        btnValidar.visibility = View.VISIBLE
                    }
                }

                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun afterTextChanged(editable: Editable) {}
            })
        }

    }


    private fun setOCR_Manual() {

        /*
        codUni.setVisibility(View.VISIBLE);
                    progressOCR.setVisibility(View.VISIBLE);
                    ttCodUni.setVisibility(View.VISIBLE);
         */

        with(binding.lyBottomSheet) {
            editTextOCR.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (charSequence.length == 13) {
                        println(charSequence)
                        typeOCR = 2
                        btnValidar.visibility = View.VISIBLE
                    }
                }

                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun afterTextChanged(editable: Editable) {}
            })
        }

    }


    private fun urlValidator(url: String?): Boolean {
        return try {
            URL(url).toURI()
            true
        } catch (exception: URISyntaxException) {
            false
        } catch (exception: MalformedURLException) {
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ActScanCodes.CODIGO_OK_QR -> {
                    val info: DatoQR? = if (data != null) {
                        data.extras!!.getParcelable("result_object")
                    } else {
                        DatoQR("N/F")
                    }
                    //tarea a realizar
                    println("Resultado  (${info?.QR})")
                    val codigo: String = info?.QR.toString()
                    if (urlValidator(codigo)) {
                        with(binding.lyBottomSheet) {
                            ttCodQR.visibility = View.VISIBLE
                            txtCodQR.visibility = View.VISIBLE
                            //codQr.setText(code.rawValue.replace("http","https"));
                            txtCodQR.text = codigo
                            txtCodQR.requestFocus()
                            btnValidar.visibility = View.VISIBLE
                        }
                    } else {
                        with(binding.lyBottomSheet) {
                            ttCodUni.visibility = View.VISIBLE
                            txtCodUni.visibility = View.VISIBLE
                            txtCodUni.text = codigo
                            txtCodUni.requestFocus()
                        }
                    }
                }
            }
        }


//          REMPLAZAR ESTA LLAMADA
/*        if (requestCode == CODIGO_INTENT_QR) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    val codigo: String = data.getStringExtra("codigo")!!


                    if (urlValidator(codigo)) {
                        with(binding.lyBottomSheet) {
                            ttCodQR.visibility = View.VISIBLE
                            txtCodQR.visibility = View.VISIBLE
                            //codQr.setText(code.rawValue.replace("http","https"));
                            txtCodQR.text = codigo
                            txtCodQR.requestFocus()
                            btnValidar.visibility = View.VISIBLE
                        }


                    } else {

                        with(binding.lyBottomSheet) {
                            ttCodUni.visibility = View.VISIBLE
                            txtCodUni.visibility = View.VISIBLE
                            txtCodUni.text = codigo
                            txtCodUni.requestFocus()
                        }

                    }
                    println("Este es el valor del codigo : $codigo")
                }
            }
        }*/


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