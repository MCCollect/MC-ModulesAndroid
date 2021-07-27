package com.mc.mcmodules.view

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.mc.mcmodules.R
import com.mc.mcmodules.model.classes.DataInfoAutorizacion
import com.mc.mcmodules.model.classes.DataInfoDomicilio
import com.mc.mcmodules.model.classes.DataInfoSolicitante
import com.mc.mcmodules.view.pinhc.activity.ActPinHC
import com.mc.mcmodules.view.scanine.ActOCRINE


@Suppress("DEPRECATION")
class ActTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test)

        val intent = Intent(this, ActPinHC::class.java)
        //startActivityForResult(intent, ActOCRINE.CODIGO_OK_INE_DATA)
        intent.putExtra("data_solicitante",DataInfoSolicitante(
            "Erick",
            "Ramos",
            "Cruz",
            "H",
            "Soltero",
            "RACE960730HDFMRR00",
            "30/07/1996"
        ))

        intent.putExtra("data_domicilio",DataInfoDomicilio(
            "Avenida Tlahuac",
            "6122",
            "No. 4",
            "13300",
            "5514181916",
            "Santaana poniente",
            "tlahuac",
            "cdmx",
            "CDMX"
        ))


        intent.putExtra("data_autorizacion", DataInfoAutorizacion(
            getString(R.string.acepto_condiciones),
            getString(R.string.text1_aceptocondiciones),
            getString(R.string.text2_aceptocondiciones),
            getString(R.string.text3_aceptocondiciones),
            "5514181916",
            "Santaana poniente"

        ))
        startActivityForResult(intent, ActOCRINE.CODIGO_OK_INE_DATA)




    }
}