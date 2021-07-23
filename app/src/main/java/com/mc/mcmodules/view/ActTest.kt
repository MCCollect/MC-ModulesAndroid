package com.mc.mcmodules.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mc.mcmodules.R

class ActTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test)

        val intent = Intent(this, ActOCRINE::class.java)
        startActivityForResult(intent, ActOCRINE.CODIGO_OK_INE_DATA)
    }
}