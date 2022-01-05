package com.mc.mcmodules.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mc.mcmodules.databinding.ActTestBinding
import com.mc.mcmodules.model.classes.data.*
import com.mc.mcmodules.view.camera.activity.ActCam
import com.mc.mcmodules.view.escanergenerico.activity.ActOCRDocs
import com.mc.mcmodules.view.pinhc.activity.ActPinHC
import com.mc.mcmodules.view.scanine.ActOCRINE
import java.io.File


@Suppress("DEPRECATION")
class ActTest : AppCompatActivity() {


    private lateinit var binding: ActTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentView()
//        val intent = Intent(this, ActCam::class.java)
        val intent = Intent(this, ActOCRDocs::class.java)
        intent.putExtra("data_docs", DataDocs(
            arrayListOf(),arrayListOf(
                "NOMBRE", "DIRECCION",
                "NO. DE SERVICIO","RMU","LIMITE DE PAGO","CORTE A PARTIR","TARIFA","NO. MEDIDOR","MULTIPLICADOR","PERIODO FACTURADO")
        ))

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
        startActivityForResult(intent, ActOCRDocs.CODIGO_OK_CFE)
/*

        val ocr = OCR(this)
        //ocr.loadBitmap(BitmapFactory.decodeResource(resources, R.drawable.text_example))
        //ocr.loadString("curp : race960730hdfmrr00")
        ocr.loadString("xcvcxvxcv vkfhn54mndamnbasd (race960730hdfmrr00)fsdkl f ñlsdflk hfsdjklh sdf")
        //binding.text.text = ocr.getTextFromREGEX(Pattern.compile(".*\\s*CURP.\\s*:\\s*.(\\w{18})\\s*.*"),1)
        binding.text.text = ocr.getTextFromREGEX(Pattern.compile(".*\\s*[(](\\w{18})[)]\\s*.*"),1)*/

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ActOCRINE.CODIGO_OK_INE_DATA -> {
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
                ActOCRDocs.CODIGO_OK_CFE -> {
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