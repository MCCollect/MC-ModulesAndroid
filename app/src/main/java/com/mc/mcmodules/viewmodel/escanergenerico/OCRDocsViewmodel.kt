package com.mc.mcmodules.viewmodel.escanergenerico


import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mc.mcmodules.model.classes.library.OCR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.regex.Pattern

class OCRDocsViewmodel : ViewModel() {
    companion object {
        private var OCRDocsViewmodel: OCRDocsViewmodel? = null

        @Synchronized
        fun getInstance(): OCRDocsViewmodel? {
            if (OCRDocsViewmodel == null) {
                throw Exception("No hay instancia creada de este viewModel(OCRCFEViewModel)")
            }
            return OCRDocsViewmodel
        }
    }

    private var textOfImage: String = ""
    private var _texto: MutableLiveData<String> = MutableLiveData()

    //val liveTexto: LiveData<String> get() = _texto
    init {
        _texto.value = ""
    }

    private lateinit var ocr: OCR
    fun initOCR(context: Context) {
        ocr = OCR(context)
    }

    fun loadDataOCR(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            ocr.loadBitmap(bitmap)
            searchData()
        }
    }

    private fun searchData() {
        textOfImage = ocr.getTextOfImage()
        _texto.postValue(textOfImage)
    }

    fun getText(): LiveData<String> {
        return _texto
    }


//            Regex que tienen diferentes grupos

    fun searchDataS(pattern: String, group: Int): String {
        return ocr.getTextFromREGEX(Pattern.compile(pattern), group)
    }
}

