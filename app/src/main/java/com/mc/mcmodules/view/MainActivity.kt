package com.mc.mcmodules.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mc.mcmodules.R
import com.mc.mcmodules.model.classes.InfoINE
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, ActOCRINE::class.java)
        startActivityForResult(intent, ActOCRINE.CODIGO_OK_INE_DATA)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        println("requestCode:::" + requestCode + " ::: " + ActOCRINE.CODIGO_OK_INE_DATA)
        println("resultCode:::$resultCode")

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ActOCRINE.CODIGO_OK_INE_DATA -> {
                    val info: InfoINE?
                    if (data != null) {
                        info = data.extras!!.getParcelable("result_object")
                    } else {
                        info = InfoINE("N/F", "", "", "", "", "", "", "")
                    }

                    println("Escaneos : ${info?.NOMBRE ?: "No result"}")
                }
            }
        }
    }
}