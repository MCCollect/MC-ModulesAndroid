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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mc.alternativasur.api.interfaces.ActivityResultHandler
import com.mc.mcmodules.R
import com.mc.mcmodules.model.classes.data.DataDocs
import com.mc.mcmodules.model.classes.data.DataCamera
import com.mc.mcmodules.model.classes.data.ItemScanner
import com.mc.mcmodules.model.classes.library.OCR
import com.mc.mcmodules.view.camera.activity.ActCam
import com.mc.mcmodules.view.escanergenerico.adapter.AdapterOCRLista
import com.mc.mcmodules.view.escanergenerico.adapter.AdapterFormDocs
import java.io.File

open class ActOCRDocs : AppCompatActivity(), ActivityResultHandler {
    companion object { const val CODIGO_OK_SCANDOCS: Int = 137 }
    private lateinit var binding: ActOcrcfeBinding
    private lateinit var dataDocs: DataDocs
    private lateinit var ocr: OCR
    private lateinit var cadena: MutableList<String>
    private var texto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActOcrcfeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        recoverDataIntent()
        initObjects()
        initBinding()
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

    private fun initObjects () {
        ocr = OCR(this)
        val intent = Intent(this, ActCam::class.java)
        startActivityForResult(intent, ActCam.CODIGO_OK_CAMERA)
    }

    private fun initBinding() {
        binding.imgcamara.setOnClickListener {
            val intent = Intent(this, ActCam::class.java)
            startActivityForResult(intent, ActCam.CODIGO_OK_CAMERA)
        }
        val layoutManager = LinearLayoutManager(this)
        binding.RecyclerFormCFE.layoutManager = layoutManager
        val listaInicial: ArrayList<ItemScanner> = getListaEtiquetas()
        val adapter = AdapterFormDocs(listaInicial, this)
        binding.RecyclerFormCFE.adapter = adapter
        binding.btnGuardarInfoCFE.setOnClickListener {
            listaInicial.forEachIndexed { index, itemScanner ->
                dataDocs.camposDocScaneado.add(itemScanner.respuest)
            }
            handleResult(dataDocs)
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

    private fun initRecyclerPrueba() {
        cadena = texto.split("\n").toMutableList()
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        cadena.forEach {
            if (it.isNotEmpty()) {
                val adapter = AdapterOCRLista(cadena, R.layout.recycler_view_item, this) {
                        texto, _ -> if (texto != null) copiar(texto)
                }
                binding.RecyclerPrueba.layoutManager = layoutManager
                binding.RecyclerPrueba.adapter = adapter
            }
        }
    }

    private fun copiar(text: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Texto copiado", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "Agregado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    private fun initData(bitmap: Bitmap) {
        ocr.loadBitmap(bitmap)
        texto = ocr.getTextOfImage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ActCam.CODIGO_OK_CAMERA -> {
                    val fileImage: DataCamera? = if (data != null) {
                        data.extras!!.getParcelable("result_object")
                    } else DataCamera("not_found_path")
                    val image = File(fileImage?.PATH!!)
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(image.absolutePath, bmOptions)
                    binding.imgReciboCFE.setImageBitmap(bitmap)
                    initData(bitmap)
                    initRecyclerPrueba()
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