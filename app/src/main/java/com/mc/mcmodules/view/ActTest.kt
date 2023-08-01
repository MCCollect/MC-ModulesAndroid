package com.mc.mcmodules.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import com.mc.mcmodules.databinding.ActTestBinding
import com.mc.mcmodules.model.classes.data.*
import com.mc.mcmodules.utils.GpsUtils
import com.mc.mcmodules.view.camera.activity.ActCam
import com.mc.mcmodules.view.escanergenerico.activity.ActOCRDocs
import com.mc.mcmodules.view.firma.FirmaActivity
import com.mc.mcmodules.view.logcat.datasource.LogsDataSource
import com.mc.mcmodules.view.logcat.views.activities.LogcatActivity
import com.mc.mcmodules.view.pinhc.activity.ActPinHC
import com.mc.mcmodules.view.scaninereverso.ActOCRINEREVERSO
import java.io.File
import java.lang.Exception

@Suppress("DEPRECATION")
class ActTest : AppCompatActivity() {

    private val TAG = this::class.java.simpleName

    private lateinit var binding: ActTestBinding

    private val ineLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            result.data?.let { data ->
                val ineObject = data.extras?.getParcelable("result_object") ?: InfoINE("N/F", "", "", "", "", "", "", "","", "", "")
                //val ineObject = data.extras?.getParcelable("result_object") ?: InfoINEReverso("", "")
                Log.d(TAG, "DEBUG:  ${ineObject.printInfo()}")
            }
        }
    }

    private val logcatLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val typeResult = data.extras?.getParcelable(LogcatActivity.RESULT_DATA_INTENT) ?:
                DataLogcatOutput(listLogs = emptyList())
                if (typeResult.listLogs.isNotEmpty()) {
                    Toast.makeText(
                        this,
                        "Enviando ${typeResult.listLogs.size} logs...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private val locationSettingsLauncher = registerForActivityResult(
        StartIntentSenderForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Se activo GPS", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Se rechazo activar GPS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun testLogcat() {
        val fileData = DataLogcatInput("log.txt", 10, true)
        val dataSource = LogsDataSource(this, fileData)
        dataSource.addNewLogRequest("Prueba consulta")
        dataSource.addNewLogResponse("Prueba respuesta")
        dataSource.addNewLogError("Prueba error")

        val intent = Intent(this, LogcatActivity::class.java)
        intent.putExtra(LogcatActivity.DATA_INTENT, fileData)
        logcatLauncher.launch(intent)
    }

    private fun checkLocationSettings() {
        try {
            val gpsUtils = GpsUtils(this)
            gpsUtils.checkLocationSettings(locationSettingsLauncher, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentView()
        testLogcat()
        //checkLocationSettings()

        /*
        val intent = Intent(this, ActOCRINE::class.java)
        ineLauncher.launch(intent)
        */

        /*

        val intent = Intent(this,FirmaActivity::class.java)
        val mypath = File(filesDir.path,"ultima.jpg")
        //val mypath = File(filesDir.path+"/R1/seccion2/r2","firma1.jpg")
        intent.putExtra("data_firma",DataFirmaRequest(mypath.absolutePath,"Anita la huerfanita"))
        startActivityForResult(intent, FirmaActivity.CODIGO_OK_FIRMA_DATA)

        */


/*//        PRUEBA DEL ESCANNER DE QR
        val intent = Intent(this, ActScanCodes::class.java)
        startActivityForResult(intent, ActScanCodes.CODIGO_OK_QR)*/
        //val intent = Intent(this, ActCam::class.java)
        //val intent = Intent(this, ActOCRDocs::class.java)

        /*val array = mutableListOf<String>()
        for (i in 0..100) {
            array.add("Test $i")
        }

        intent.putExtra(
            "data_docs", DataDocs(
                arrayListOf(),
                ArrayList(array)
            )
        )

        startActivityForResult(intent, ActOCRDocs.CODIGO_OK_SCANDOCS)*/

/*

        intent.putExtra(
            "data_solicitante", DataInfoSolicitante(
                "Erick",
                "Ramos",
                "Cruz",
                "H",
                "Soltero",
                "RACE960730HDFMRR00",
                "30/07/1996",
                arrayListOf(
                    false,
                    false,
                    false,
                    true,
                    true,
                    true,
                    true
                ) //puedes poner un arreglo vacio no se bloqueara nada
            )
        )

        intent.putExtra(
            "data_domicilio", DataInfoDomicilio(
                "Avenida Tlahuac",
                "6122",
                "No. 4",
                "13300",
                "5514181916",
                "Santaana poniente",
                "tlahuac",
                "cdmx",
                "CDMX",
                arrayListOf(
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true
                )//puedes poner un arreglo vacio no se bloqueara nada
            )
        )


        intent.putExtra(
            "data_autorizacion", DataInfoAutorizacion(
                getString(R.string.acepto_condiciones),
                getString(R.string.text1_aceptocondiciones),
                getString(R.string.text2_aceptocondiciones),
                getString(R.string.text3_aceptocondiciones),
                "https://www.facebook.com/",
                "api/CirculoCredito/pin",
                "https://dev.mcnoc.mx/WsMovilAlternativaSur/",
                "ALTERNATIVASUR",
                "INTEGRANTE",
                filesDir.path+"/firma.jpg",
                "5576897654"

            )
        )*/
//        startActivityForResult(intent, ActCam.CODIGO_OK_CAMERA)

/*

        val ocr = OCR(this)
        //ocr.loadBitmap(BitmapFactory.decodeResource(resources, R.drawable.text_example))
        //ocr.loadString("curp : race960730hdfmrr00")
        ocr.loadString("xcvcxvxcv vkfhn54mndamnbasd (race960730hdfmrr00)fsdkl f ñlsdflk hfsdjklh sdf")
        //binding.text.text = ocr.getTextFromREGEX(Pattern.compile(".*\\s*CURP.\\s*:\\s*.(\\w{18})\\s*.*"),1)
        binding.text.text = ocr.getTextFromREGEX(Pattern.compile(".*\\s*[(](\\w{18})[)]\\s*.*"),1)*/


        /*
        val cipherAES = CipherAES()

        val objectAES = cipherAES.encrypt("Hola comó estas")

        println("Texto cifrado : " + objectAES.ciphertext)
        println("Texto descifrado : " + cipherAES.decrypt(objectAES.ciphertext, objectAES.iv))*/


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ActOCRINEREVERSO.CODIGO_OK_INE_DATA -> {
                    val info: InfoINE? = if (data != null) {
                        data.extras!!.getParcelable("result_object")
                    } else {
                        InfoINE("N/F", "", "", "", "", "", "", "", "", "", "")
                    }
                    //tarea a realizar
                }
                ActPinHC.CODIGO_OK_HC_DATA -> {
                    val pinRequest: PINRequest? = if (data != null) {
                        data.extras!!.getParcelable("result_object")
                    } else {
                        PINRequest(0, "Fail", "")
                    }
                    //tarea a realizar
                    println("Resultado del PIN (${pinRequest?.PIN}): ${pinRequest?.RESULT}")

                    if (pinRequest?.PIN == -1000) {

                        val image = File(pinRequest.FIRMA)
                        val bmOptions = BitmapFactory.Options()
                        var bitmap = BitmapFactory.decodeFile(image.absolutePath, bmOptions)
                        bitmap = Bitmap.createScaledBitmap(
                            bitmap!!,
                            200,
                            200,
                            true
                        )
                        binding.imgFirmaTest.setImageBitmap(bitmap)
                    }
                }
                ActCam.CODIGO_OK_CAMERA -> {
                    val fileImage: DataCamera? = if (data != null) {
                        data.extras!!.getParcelable("result_object")
                    } else {
                        DataCamera("not_found_path")
                    }

                    val image = File(fileImage?.PATH!!)
                    val bmOptions = BitmapFactory.Options()
                    var bitmap = BitmapFactory.decodeFile(image.absolutePath, bmOptions)
                    bitmap = Bitmap.createScaledBitmap(
                        bitmap!!,
                        200,
                        200,
                        true
                    )
                    binding.imgFirmaTest.setImageBitmap(bitmap)


                }
                ActOCRDocs.CODIGO_OK_SCANDOCS -> {
                    val info: DataDocs? = if (data != null) {
                        data.extras!!.getParcelable("result_object")
                    } else {
                        DataDocs(arrayListOf(), arrayListOf())
                    }
                    //tarea a realizar
                    /*        println("Resultado del Data (${info?.camposCFE}")

                            binding.text.setText("${info?.camposCFE}")*/
                    info?.camposDocScaneado?.forEachIndexed { index, itemScanner ->
                        println("Respuesta ${index}: " + itemScanner)
                    }
                }
                FirmaActivity.CODIGO_OK_FIRMA_DATA -> {
                    val dataResponse: DataFirmaResponse? = if (data != null) {
                        data.extras!!.getParcelable("result_object")
                    } else DataFirmaResponse()
                    if (dataResponse != null) {
                        Log.i(" CODIGO_OK_FIRMA_DATA ", "status: ${dataResponse.isOk}, mensaje: ${dataResponse.message}")
                        if (dataResponse.isOk) {
                            val file = File(filesDir.path,"ultima.jpg")
                            val uri = Uri.fromFile(file)
                            binding.imgFirmaTest.setImageURI(uri)
                        }
                    }
                }
/*                ActScanCodes.CODIGO_OK_QR -> {
                    val info: DatoQR? = if (data != null) {
                        data.extras!!.getParcelable("result_object")
                    } else {
                        DatoQR("N/F")
                    }
                    //tarea a realizar
                    println("Resultado  (${info?.QR})")
                }*/
            }
        }
    }

    private fun initContentView() {
        binding = ActTestBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val view = binding.root
        setContentView(view)
    }

}