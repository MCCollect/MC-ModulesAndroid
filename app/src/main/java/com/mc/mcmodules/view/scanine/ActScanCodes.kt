package com.mc.mcmodules.view.scanine

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.mc.alternativasur.api.interfaces.ActivityResultHandler
import com.mc.mcmodules.R
import com.mc.mcmodules.model.classes.data.DatoQR
import java.io.IOException

class ActScanCodes : AppCompatActivity(), ActivityResultHandler {
    companion object {
        const val CODIGO_OK_QR: Int = 177
    }
    /*INICIALIZAR VARIABLES*/
    private var cameraSource: CameraSource? = null
    private var cameraView: SurfaceView? = null

    private val MY_PERMISSIONS_REQUEST_CAMERA = 1

    private var token = ""
    private var tokenanterior = ""

    private lateinit var datoQR: DatoQR
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_scan_codes)
        cameraView = findViewById<View>(R.id.camera_view) as SurfaceView
        datoQR = DatoQR("")
        initQR()
    }
    fun initQR() {
//         Crear detector qr
        val barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS) //Tipo de QR
            .build()
//         Crear la camara
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1600, 1024)
            .setAutoFocusEnabled(true) //Se debe agregar esta caracteristica
            .build()
//         listener de ciclo de vida de la camara
        cameraView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
//                 Verificar los permisos
                if (ActivityCompat.checkSelfPermission(this@ActScanCodes, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                 Validar que la version de android sea al menos la M
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)
                    }
                } else {
//                  Usar la camara
                    try {
                        cameraSource!!.start(cameraView!!.holder)
                    } catch (ie: IOException) {
                        Log.e("CAMERA SOURCE", ie.message.toString())
                    }
                }
            }
            /*IMPLEMENTAR METODOS NECESARIOS DEL SURFACEHOLDER.CALLBACK*/
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) { cameraSource!!.stop() }
        })

//         Detector de QR
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            /*IMPLEMENTAR METODOS NECESARIOS DEL Detector.Processor*/
            override fun release() {}
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
//                Se obtiene el TOKEN = Dato recupardo del SCANNER
                if (barcodes.size() > 0) {
                    token = barcodes.valueAt(0).displayValue.toString()
//                     Verificar que el token anterior no se igual al actual y evitar multiples llamadas empleando el mismo token
                    if (token != tokenanterior) {
//                         Guarda el ultimo token procesado
                        tokenanterior = token
                        Log.i("token", token)
//                      Retornar el valor
//                          Valor que obtiene
                        datoQR.QR = token
                        handleResult(datoQR)
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
//        escanerZXing.setResultHandler(this);
//        escanerZXing.startCamera(); // Comenzar la cámara en onResume
    }
    override fun onPause() {
        super.onPause()
//        escanerZXing.stopCamera(); // Pausar en onPause
    }

    override fun handleResult(parcelable: Parcelable) {
        val intentRegreso = Intent()
        intentRegreso.putExtra("result_object", parcelable)
        setResult(RESULT_OK, intentRegreso)
        this.finish()
    }

}