package com.mc.mcmodules.view.escanergenerico.activity

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mc.mcmodules.databinding.ActOcrcfeBinding
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mc.alternativasur.api.interfaces.ActivityResultHandler
import com.mc.mcmodules.R
import com.mc.mcmodules.model.classes.data.DataDocs
import com.mc.mcmodules.model.classes.data.DataCamera
import com.mc.mcmodules.model.classes.data.ItemScanner
import com.mc.mcmodules.view.camera.activity.ActCam
import com.mc.mcmodules.view.escanergenerico.adapter.AdapterOCRLista
import com.mc.mcmodules.view.escanergenerico.adapter.AdapterFormDocs
import com.mc.mcmodules.viewmodel.escanergenerico.OCRDocsViewmodel
import java.io.File

open class ActOCRDocs : AppCompatActivity(), ActivityResultHandler {
    companion object {
        const val CODIGO_OK_SCANDOCS: Int = 137
    }

    private lateinit var binding: ActOcrcfeBinding
    private lateinit var selfViewModel: OCRDocsViewmodel
    private lateinit var cadena: MutableList<String>
    private lateinit var dataDocs: DataDocs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActOcrcfeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        recoverDataIntent()
        //INICIALIZAR LA CÄMARA PRIMERO
        val intent = Intent(this, ActCam::class.java)
        startActivityForResult(intent, ActCam.CODIGO_OK_CAMERA)
        //Instancia para persistir datos
        selfViewModel = ViewModelProvider(this).get(OCRDocsViewmodel::class.java)
        initContentView()
        initObserver()
    }


    /*Metodos al inicar la vista*/
    private fun initContentView() {
        binding.imgcamara.setOnClickListener {
            val intent = Intent(this, ActCam::class.java)
            startActivityForResult(intent, ActCam.CODIGO_OK_CAMERA)
        }
        val layoutManager = LinearLayoutManager(this)
        binding.RecyclerFormCFE.layoutManager = layoutManager
        val listaInicial: ArrayList<ItemScanner> = getListaEtiquetas()
        val adapter = AdapterFormDocs(listaInicial, R.layout.view_item_cfe, this)
        binding.RecyclerFormCFE.adapter = adapter

        binding.btnGuardarInfoCFE.setOnClickListener {
            listaInicial.forEachIndexed { index, itemScanner ->
                dataDocs.camposDocScaneado.add(itemScanner.respuest)
            }
            handleResult(dataDocs)
//            println("Respuesta: "+listaInicial.size)
        }
    }

    fun getListaEtiquetas(): ArrayList<ItemScanner> {
        val lista = arrayListOf<ItemScanner>()
        println("Tamaño " + dataDocs.camposDoc.size)
        dataDocs.camposDoc.forEach { item ->
            lista.add(ItemScanner(item, ""))
            println("Pregunta: " + item)
        }
        return lista
    }

    /*CREAR UN OBSERVE PARA SUSCRIBIRSE AL CAMBIO EN LA VISTA
    * LO QUE RECONOCE EL OCR*/
    private fun initObserver() {
        selfViewModel.getText().observe(this, { v ->
            cadena = v.split("\n").toMutableList()
            /* **************** VISTA D I N A M I C A *****************/
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            cadena.forEach {
                if (it.isNotEmpty()) {
//                    binding.LinearPaste.visibility = View.VISIBLE
                    val adapter =
                        AdapterOCRLista(
                            cadena,
                            R.layout.recycler_view_item,
                            this
                        ) { texto, _ ->
                            if (texto != null) {
                                copiar(texto)
                            }
                        }
                    binding.RecyclerPrueba.layoutManager = layoutManager
                    binding.RecyclerPrueba.adapter = adapter
                }
            }
        })
    }

    /*METODO PARA COPIAR*/
    private fun copiar(text: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Texto copiado", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "Agregado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    /*LLEVAR LOS DATOS DE LA IMAGEM HACIA EL VIEW MODEL*/
    private fun initData(bitmap: Bitmap) {
        selfViewModel.initOCR(this)
        selfViewModel.loadDataOCR(bitmap)
    }

    /*PARA TOMAR UNA FOTOGRAFIA*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ActCam.CODIGO_OK_CAMERA -> {
                    val fileImage: DataCamera? = if (data != null) {
                        data.extras!!.getParcelable("result_object")
                    } else {
                        DataCamera("not_found_path")
                    }
                    val image = File(fileImage?.PATH!!)
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(image.absolutePath, bmOptions)
                    binding.imgReciboCFE.setImageBitmap(bitmap)
                    initData(bitmap)
                }
            }
        }
    }

    private fun recoverDataIntent() {
        val intent = intent
        if (intent == null) {
            throw java.lang.Exception("Los datos para el formulario son obligatorios (2)")
        } else {
            val dataFormulario = intent.getParcelableExtra<DataDocs>("data_docs")
            if (dataFormulario == null) {
                throw java.lang.Exception("Los datos para el formulario no pueden ser nulos")
            } else {
                if (dataFormulario.camposDoc.size > 0) {
                    this.dataDocs = dataFormulario
                } else {
                    throw java.lang.Exception("La lista del formulario no puede estar vacia")
                }
            }
        }
    }

    override fun handleResult(parcelable: Parcelable) {
        val intentRegreso = Intent()
        intentRegreso.putExtra("result_object", parcelable)
        setResult(RESULT_OK, intentRegreso)
        this.finish()
    }
}