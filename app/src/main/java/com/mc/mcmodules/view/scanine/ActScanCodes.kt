package com.mc.mcmodules.view.scanine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class ActScanCodes : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private lateinit var escanerZXing: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentView()
    }


    private fun initContentView() {
        //selfViewModel = ViewModelProvider(this).get(OCRINEViewmodel::class.java)
        //binding = ActScanCodesBinding.inflate(layoutInflater)
        //binding.lifecycleOwner = this
        escanerZXing = ZXingScannerView(this);
        escanerZXing.setFormats(ZXingScannerView.ALL_FORMATS)
        // Hacer que el contenido de la actividad sea el escaner
        setContentView(escanerZXing);

    }

    override fun onResume() {
        super.onResume()
        escanerZXing.setResultHandler(this);
        escanerZXing.startCamera(); // Comenzar la cámara en onResume
    }

    override fun handleResult(rawResult: Result?) {
        // Si quieres que se siga escaneando después de haber leído el código, descomenta lo siguiente:
        // Si la descomentas no recomiendo que llames a finish
//        escanerZXing.resumeCameraPreview(this);
        // Obener código/texto leído
        // Si quieres que se siga escaneando después de haber leído el código, descomenta lo siguiente:
        // Si la descomentas no recomiendo que llames a finish
//        escanerZXing.resumeCameraPreview(this);
        // Obener código/texto leído
        val codigo: String = rawResult?.text ?: ""
        // Preparar un Intent para regresar datos a la actividad que nos llamó
        // Preparar un Intent para regresar datos a la actividad que nos llamó
        val intentRegreso = Intent()
        println("Codigo del escaneo : $codigo")
        intentRegreso.putExtra("codigo", codigo)
        setResult(RESULT_OK, intentRegreso)
        finish()
    }


    override fun onPause() {
        super.onPause()
        escanerZXing.stopCamera(); // Pausar en onPause
    }

}