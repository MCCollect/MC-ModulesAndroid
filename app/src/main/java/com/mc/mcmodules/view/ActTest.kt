package com.mc.mcmodules.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.ActTestBinding
import com.mc.mcmodules.model.classes.data.*
import com.mc.mcmodules.model.classes.library.OCR
import com.mc.mcmodules.view.firmae.ActPinE
import com.mc.mcmodules.view.pinhc.activity.ActPinHC
import com.mc.mcmodules.view.scanine.ActOCRINE
import java.io.File
import java.util.regex.Pattern


@Suppress("DEPRECATION")
class ActTest : AppCompatActivity() {


    private lateinit var binding: ActTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentView()

        val intent = Intent(this, ActPinE::class.java)

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

*/
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
        )
        //startActivityForResult(intent, ActPinHC.CODIGO_OK_HC_DATA)
        startActivityForResult(intent, ActPinE.CODIGO_OK_E_DATA)
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
                        InfoINE("N/F", "", "", "", "", "", "", "","","","")
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