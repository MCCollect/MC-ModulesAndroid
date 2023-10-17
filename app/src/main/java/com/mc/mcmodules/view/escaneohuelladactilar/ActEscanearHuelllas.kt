package com.mc.mcmodules.view.escaneohuelladactilar

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.identy.Attempt
import com.identy.FingerAS
import com.identy.IdentyError
import com.identy.IdentyResponse
import com.identy.IdentyResponseListener
import com.identy.IdentySdk
import com.identy.TemplateSize
import com.identy.WSQCompression
import com.identy.enums.Finger
import com.identy.enums.FingerDetectionMode
import com.identy.enums.Hand
import com.identy.enums.Template
import com.mc.alternativasur.api.interfaces.ActivityResultHandler
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.ActEscanearHuelllasBinding
import com.mc.mcmodules.model.classes.data.DataFingerScan
import com.mc.mcmodules.model.classes.data.DataFingerScanResult
import com.mc.mcmodules.model.classes.library.ResFingerScan
import com.mc.mcmodules.view.permissions.view.activities.gone
import com.mc.mcmodules.view.permissions.view.activities.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Módulo para escanear huellas mediante el SDK de FIMPE
 *
 * El módulo en esta versión es capáz de escanear las huellas dactilares de los dedos indices
 * de cada mano mediante la cámara del dispositivo sin necesidad de emplear hardware
 * de terceros para la captura de las mismas.
 *
 * El módulo para funcionar necesita 3 parámetros de tipo String que son pasados mediante la data
 * del intent al invocarlo en forma de una instancia de la clase "DataFingerScan" en la cual se
 * encuentran con el nombre de :
 *
 * @param pathImageIR
 * @param pathImageIL
 * @param pathLicSDK
 *
 * Cuando finaliza la ejecución del módulo y si todas las tareas se ejecutaron correctamente el
 * módulo regesará una instancia de la clase DataFingerScanReSult donde, en esta versión, se
 * regresará en el arreglo de la instancia antes mencionada 2 objetos de tipo DataFingerScanned
 * con la información de los dedos capturados previamente comenzando por los resultados del dedo
 * índice derecho.
 *
 * **/
@Suppress("DEPRECATION")
class ActEscanearHuelllas : AppCompatActivity(), ActivityResultHandler {

    companion object {
        const val DATA_INTENT = "data_fingerscan"
        const val RESULT_DATA_INTENT = "result_data_scan"
    }

    private lateinit var binding: ActEscanearHuelllasBinding
    private lateinit var detectionModes: Array<FingerDetectionMode>
    private val scope by lazy { CoroutineScope(SupervisorJob()) }
    private val formatter: NumberFormat = DecimalFormat("#0.00")


    /**
     * @param dataScan parámetro de configuracion inicial del moódulo.
     * @param dataResult parámetro para regresar los resultados obtenidos de la captura de huellas,
     * se inicializa con una lista de dos objetos (ver inicialización en el metodo initObjects())
     * para que se pueda comenzar a utilizar desde que se escana la primer huella y aplicar el orden
     * de la respuesta (Siempre indice derecho primero).
     *
     * **/
    private lateinit var dataScan: DataFingerScan
    private lateinit var dataResult: DataFingerScanResult


    /**
     * Nombre de los posibles dedos escaneados, en un futuro estos aumentarán , en esta versión solo
     * se manejarán los dedos indices de cada mano.
     *
     * **/
    val index_r = "INDEX_RIGTH"
    val index_l = "INDEX_LEFT"


    private var json: JSONObject? = null
    private val gson = Gson()
    var responseScanR: ResFingerScan? = null
    var responseScanL: ResFingerScan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObjects()
        recoverDataIntent()
        initListeners()
    }

    private fun initObjects() {
        val results = ArrayList<DataFingerScanResult.DataFingerScanned>()
        results.add(
            DataFingerScanResult.DataFingerScanned(
                "",
                0.0,
                index_r
            )
        )
        results.add(
            DataFingerScanResult.DataFingerScanned(
                "",
                0.0,
                index_l
            )
        )
        dataResult = DataFingerScanResult(results)
    }

    private fun initBinding() {
        binding = ActEscanearHuelllasBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    /**
     * recoverDataIntent()
     *
     * Método que valida que todos los parámatros obligatorios que se pasan mediante intent y que
     * son necesarios para que este módulo funcione correctamente y en caso de que falte alguno,
     * se lanzará una excepción con la descripción del o los datos faltantes.
     *
     * **/
    private fun recoverDataIntent() {
        val intent = intent
        if (intent == null) {
            throw java.lang.Exception("No se encontro la información de licencias ni las rutas de guardado")
        } else {
            val data = intent.getParcelableExtra<DataFingerScan>(DATA_INTENT)
            if (data == null) {
                throw java.lang.Exception("Los datos de guardado y licencia son obligatorios")
            } else {
                if (data.pathImageIL.isNotEmpty() && data.pathImageIR.isNotEmpty() &&
                    data.pathLicSDK.isNotEmpty()) {
                    this.dataScan = data
                } else {
                    throw java.lang.Exception("Todos los paths son obligatotios, no pueden ir vacios")
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnCaptureFR.setOnClickListener { button ->
            button.isEnabled = false
            binding.zoomFingerScanned.setImageBitmap(null)
            configureCaptureIndexR()
        }

        binding.btnCaptureFL.setOnClickListener { button ->
            button.isEnabled = false
            binding.zoomFingerScanned.setImageBitmap(null)
            configureCaptureIndexL()
        }

        binding.btnFinalizarCaptura.setOnClickListener {
            handleResult(dataResult)
        }

        binding.indexRImage.setOnClickListener {
            binding.zoomFingerScanned.setImageBitmap(null)
            responseScanR?.data?.rightindex?.templates?.PNG?.DEFAULT?.let { FRImage ->

                val decodedString: ByteArray =
                    Base64.decode(
                        FRImage,
                        Base64.NO_WRAP
                    )


                val inputStream: InputStream =
                    ByteArrayInputStream(decodedString)
                val bitmap = BitmapFactory.decodeStream(inputStream)


                binding.zoomFingerScanned.setImageBitmap(bitmap)
            }
        }

        binding.indexLImage.setOnClickListener {
            binding.zoomFingerScanned.setImageBitmap(null)
            responseScanL?.data?.leftindex?.templates?.PNG?.DEFAULT?.let { FLImage ->

                val decodedString: ByteArray =
                    Base64.decode(
                        FLImage,
                        Base64.NO_WRAP
                    )


                val inputStream: InputStream =
                    ByteArrayInputStream(decodedString)
                val bitmap = BitmapFactory.decodeStream(inputStream)


                binding.zoomFingerScanned.setImageBitmap(bitmap)
            }
        }
    }

    /**
     * configureCaptureIndexR()
     *
     * 1.- En el método se encuentran configuraciones repitadas que se pueden encontrar en el
     * método hermano configureCaptureIndexL, en esta versión hacer caso omiso ya que se esta
     * evaluando que cada dedo tenga configuraciones diferentes
     *
     * **/
    private fun configureCaptureIndexR() {
        val base64encoding = Base64.DEFAULT

        val templatesConfig = HashMap<Template, HashMap<Finger, ArrayList<TemplateSize>>>()
        val fingerSizeHP = HashMap<Finger, ArrayList<TemplateSize>>()

        val sizes = ArrayList<TemplateSize>()
        sizes.add(TemplateSize.DEFAULT)
        fingerSizeHP[Finger.INDEX] = sizes
        templatesConfig[Template.PNG] = fingerSizeHP
        val displayboxes = false
        val compression = WSQCompression.WSQ_10_1


        IdentySdk.newInstance(
            this, dataScan.pathLicSDK,
            { d ->
                updateIntentForIR()
                d?.base64EncodingFlag = base64encoding
                d?.setDisplayImages(displayboxes)?.setRequiredTemplates(templatesConfig)
                    ?.setDisplayBoxes(displayboxes)?.setWSQCompression(compression)
                    ?.setDetectionMode(detectionModes)
                d?.setAsSecMode(FingerAS.BALANCED_HIGH)
                d?.setDebug(false)
                d?.isAllowTabletLandscape = true
                d?.setAllowHandChange(false)
                d?.setQC { true }
                d?.enroll()
            },
            object : IdentyResponseListener {
                override fun onAttempt(p0: Hand?, p1: Int, map: MutableMap<Finger, Attempt>?) {
                    if (map != null) {
                        Log.d("NFIQ", map[Finger.INDEX]?.nfiq1Score.toString())
                        Log.d("Spoof Score", map[Finger.INDEX]?.spoofScore.toString())
                        Log.d("Capture Time", map[Finger.INDEX]?.captureTime.toString())
                        Log.d("Quality Failed?", map[Finger.INDEX]?.isQualityFailed.toString())
                        Log.d("Score Index?", map[Finger.INDEX]?.score.toString())
                        Log.d("Score Middle?", map[Finger.MIDDLE]?.score.toString())
                        Log.d("Score Ring?", map[Finger.RING]?.score.toString())
                        Log.d("Score Little?", map[Finger.LITTLE]?.score.toString())
                    }
                }

                override fun onResponse(
                    identyResponse: IdentyResponse?,
                    hashSet: HashSet<String>?
                ) {

                    scope.launch(Dispatchers.Main) {
                        with(binding) {
                            progressRimage.visible()
                            indexRImage.setImageBitmap(null)
                        }
                    }

                    if (hashSet != null) {

                        Log.d("OR. Transaction ID", hashSet.toString())
                        val res: String =
                            identyResponse?.toJson(this@ActEscanearHuelllas.baseContext)
                                .toString()
                        try {
                            val mainFolder = filesDir
                                .toString() + "/IDENTY/finger_last_response.json"
                            val dest = File(mainFolder)
                            val o = FileOutputStream(dest)
                            o.write(res.toByteArray())
                            o.close()

                            // Instantiate a JSON object from the request response
                            json = JSONObject(res)
                            responseScanR = gson.fromJson(
                                json.toString(),
                                ResFingerScan::class.java
                            )

                            responseScanR?.data?.rightindex?.templates?.PNG?.DEFAULT?.let { FRImage ->

                                val decodedString: ByteArray =
                                    Base64.decode(
                                        FRImage,
                                        Base64.NO_WRAP
                                    )


                                val inputStream: InputStream =
                                    ByteArrayInputStream(decodedString)
                                val bitmap = BitmapFactory.decodeStream(inputStream)


                                val dateGPS: DateFormat = SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm:ss",
                                    Locale.getDefault()
                                )
                                val locationDate = Date()
                                val timeOfCapture = dateGPS.format(locationDate)

                                val pathFR = File(
                                    dataScan.pathImageIR,
                                    "$timeOfCapture _RIndexFinger.png"
                                ).tryMkdir().writeBitmap(
                                    bitmap,
                                    Bitmap.CompressFormat.PNG,
                                    100
                                )

                                val calidad = formatter.format(
                                    responseScanR?.data?.rightindex?.spoof_score
                                        ?: 0.0
                                ).toDouble()

                                dataResult.results[0].pathResult = pathFR
                                dataResult.results[0].calidad = calidad



                                scope.launch(Dispatchers.Main) {
                                    with(binding) {
                                        progressRimage.gone()
                                        indexRImage.setImageBitmap(bitmap)
                                        txtStatusR.text = getString(R.string.capturada)
                                        txtCalidadR.text = calidad.toString()
                                    }
                                }


                            }


                        } catch (e: Exception) {
                            Log.d("canCreateJSON", "canCreateJSON")
                            e.printStackTrace()
                        }


                        scope.launch(Dispatchers.Main) {
                            binding.btnCaptureFR.isEnabled = true
                        }

                    }
                }

                override fun onErrorResponse(
                    identyError: IdentyError?,
                    hashSet: HashSet<String>?
                ) {
                    if (hashSet != null) {
                        val errorIdenty: String = identyError?.error.toString()
                        Log.d("OER. ERROR!!!!!", errorIdenty)
                    }
                    binding.btnCaptureFR.isEnabled = true

                }

            },
            true,
            true
        )
    }


    /**
     * configureCaptureIndexL()
     *
     * 1.- En el método se encuentran configuraciones repitadas que se pueden encontrar en el
     * método hermano configureCaptureIndexR, en esta versión hacer caso omiso ya que se esta
     * evaluando que cada dedo tenga configuraciones diferentes
     *
     * **/
    private fun configureCaptureIndexL() {
        val base64encoding = Base64.DEFAULT

        val templatesConfig = HashMap<Template, HashMap<Finger, ArrayList<TemplateSize>>>()
        val fingerSizeHP = HashMap<Finger, ArrayList<TemplateSize>>()

        val sizes = ArrayList<TemplateSize>()
        sizes.add(TemplateSize.DEFAULT)
        fingerSizeHP[Finger.INDEX] = sizes
        templatesConfig[Template.PNG] = fingerSizeHP
        val displayboxes = false
        val compression = WSQCompression.WSQ_10_1


        IdentySdk.newInstance(
            this, "2632-com.mc.demofimpe-27-10-2023.lic",
            { d ->
                updateIntentForIL()
                d?.base64EncodingFlag = base64encoding
                d?.setDisplayImages(displayboxes)?.setRequiredTemplates(templatesConfig)
                    ?.setDisplayBoxes(displayboxes)?.setWSQCompression(compression)
                    ?.setDetectionMode(detectionModes)
                d?.setAsSecMode(FingerAS.BALANCED_HIGH)
                d?.setDebug(false)


                d?.isAllowTabletLandscape = true
                d?.setAllowHandChange(false)
                d?.setQC { true }
                d?.enroll()
            },
            object : IdentyResponseListener {
                override fun onAttempt(p0: Hand?, p1: Int, map: MutableMap<Finger, Attempt>?) {
                    if (map != null) {
                        Log.d("NFIQ", map[Finger.INDEX]?.nfiq1Score.toString())
                        Log.d("Spoof Score", map[Finger.INDEX]?.spoofScore.toString())
                        Log.d("Capture Time", map[Finger.INDEX]?.captureTime.toString())
                        Log.d("Quality Failed?", map[Finger.INDEX]?.isQualityFailed.toString())
                        Log.d("Score Index?", map[Finger.INDEX]?.score.toString())
                        Log.d("Score Middle?", map[Finger.MIDDLE]?.score.toString())
                        Log.d("Score Ring?", map[Finger.RING]?.score.toString())
                        Log.d("Score Little?", map[Finger.LITTLE]?.score.toString())
                    }
                }

                override fun onResponse(
                    identyResponse: IdentyResponse?,
                    hashSet: HashSet<String>?
                ) {

                    scope.launch(Dispatchers.Main) {
                        with(binding) {
                            progressLimage.visible()
                            indexLImage.setImageBitmap(null)
                        }
                    }

                    if (hashSet != null) {

                        Log.d("OR. Transaction ID", hashSet.toString())
                        val res: String =
                            identyResponse?.toJson(this@ActEscanearHuelllas.baseContext)
                                .toString()
                        try {
                            val mainFolder = filesDir
                                .toString() + "/IDENTY/finger_last_response.json"
                            val dest = File(mainFolder)
                            val o = FileOutputStream(dest)
                            o.write(res.toByteArray())
                            o.close()

                            // Instantiate a JSON object from the request response
                            json = JSONObject(res)
                            responseScanL = gson.fromJson(
                                json.toString(),
                                ResFingerScan::class.java
                            )

                            responseScanL?.data?.leftindex?.templates?.PNG?.DEFAULT?.let { FIImage ->

                                val decodedString: ByteArray =
                                    Base64.decode(
                                        FIImage,
                                        Base64.NO_WRAP
                                    )


                                val inputStream: InputStream =
                                    ByteArrayInputStream(decodedString)
                                val bitmap = BitmapFactory.decodeStream(inputStream)


                                val dateGPS: DateFormat = SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm:ss",
                                    Locale.getDefault()
                                )
                                val locationDate = Date()
                                val timeOfCapture = dateGPS.format(locationDate)

                                val pathFL = File(
                                    dataScan.pathImageIL,
                                    "$timeOfCapture _LIndexFinger.png"
                                ).tryMkdir().writeBitmap(
                                    bitmap,
                                    Bitmap.CompressFormat.PNG,
                                    100
                                )

                                val calidad = formatter.format(
                                    responseScanL?.data?.leftindex?.spoof_score ?: 0.0
                                ).toDouble()

                                dataResult.results[1].pathResult = pathFL
                                dataResult.results[1].calidad = calidad

                                scope.launch(Dispatchers.Main) {
                                    with(binding) {
                                        binding.progressLimage.gone()
                                        binding.indexLImage.setImageBitmap(bitmap)
                                        txtStatusL
                                            .text = getString(R.string.capturada)
                                        txtCalidadL.text = calidad.toString()
                                    }
                                }


                            }


                        } catch (e: Exception) {
                            Log.d("canCreateJSON", "canCreateJSON")
                            e.printStackTrace()
                        }

                        scope.launch(Dispatchers.Main) {
                            binding.btnCaptureFL.isEnabled = true
                        }


                    }
                }

                override fun onErrorResponse(
                    identyError: IdentyError?,
                    hashSet: HashSet<String>?
                ) {
                    if (hashSet != null) {
                        val errorIdenty: String = identyError?.error.toString()
                        Log.d("OER. ERROR!!!!!", errorIdenty)
                    }
                    binding.btnCaptureFL.isEnabled = true

                }

            },
            true,
            true
        )
    }

    private fun updateIntentForIR() {
        val modes = java.util.ArrayList<FingerDetectionMode>()
        modes.add(FingerDetectionMode.RIGHT_INDEX)
        detectionModes = modes.toTypedArray()
    }

    private fun updateIntentForIL() {
        val modes = java.util.ArrayList<FingerDetectionMode>()
        modes.add(FingerDetectionMode.LEFT_INDEX)
        detectionModes = modes.toTypedArray()
    }


    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun File.writeBitmap(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int
    ): String {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }

        return this.path
    }
    private fun File.tryMkdir(): File {
       this.parentFile?.mkdirs()
        return  this
    }

    override fun handleResult(parcelable: Parcelable) {
        val intentRegreso = Intent()
        intentRegreso.putExtra(RESULT_DATA_INTENT, parcelable)
        setResult(RESULT_OK, intentRegreso)
        this.finish()
    }
}