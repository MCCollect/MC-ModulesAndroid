package com.mc.mcmodules.viewmodel.scanine

import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.vision.text.TextBlock
import com.mc.mcmodules.model.classes.data.InfoINE
import com.mc.mcmodules.model.classes.data.InfoINEReverso
import com.mc.mcmodules.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class OCRINEREVERSOViewmodel : ViewModel() {
    /***
     * Campos -> valores estaticos finales que sirven para saber que campo se necesita modificar
     * */
    companion object {
        const val FIELD_CIC = 21
        const val FIELD_IDENTIFICADOR_C = 22
    }

    private var _liveCic: MutableLiveData<String> = MutableLiveData()
    val liveCic: LiveData<String> get() = _liveCic

    private var _liveIdentificadorC: MutableLiveData<String> = MutableLiveData()
    val liveIdentificadorC: LiveData<String> get() = _liveIdentificadorC

    private var _liveTotalCampos: MutableLiveData<Int> = MutableLiveData()
    val liveTotalCampos: LiveData<Int> get() = _liveTotalCampos

    init {
        _liveCic.value = ""
        _liveIdentificadorC.value = ""
        _liveTotalCampos.value = 0
    }

    fun updateCampo(field: Int, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (field) {
                FIELD_CIC -> {
                    _liveCic.postValue(value)
                }

                FIELD_IDENTIFICADOR_C -> {
                    _liveIdentificadorC.postValue(value)
                }
            }
        }
    }

    fun startThreadOCR(items: SparseArray<TextBlock?>) {
        viewModelScope.launch(Dispatchers.Default) {
            val pattern = Pattern.compile("\\d{9}")
            val stringBuilder = StringBuilder()
            for (i in 0 until items.size()) {
                val item = items.valueAt(i)
                stringBuilder.append(item?.value)
                stringBuilder.append("\n")
            }

            val stringDetetions = stringBuilder.toString().replace("\n", " ")
                .uppercase(Locale.getDefault())

            println("Detectios $stringDetetions")

            if (_liveCic.value.equals("")) {
                println("Buscando cic")
                val patternCic = Pattern.compile(".IDMEX(.*?)<<.")
                val matcherCic = patternCic.matcher(stringDetetions)
                if (matcherCic.find()) {
                    if (matcherCic.group(1)?.uppercase(Locale.getDefault())!= null) {
                        val value = matcherCic.group(1)?.uppercase(Locale.getDefault())!!
                            .substring(0,matcherCic.group(1)?.length!!-1)
                        if(pattern.matcher(value).find()) _liveCic.postValue(value)
                        else _liveCic.postValue("")
                     } else _liveCic.postValue("")
                } else  _liveCic.postValue("")
            }

            if (_liveIdentificadorC.value.equals("")) {
                println("Buscando identificador ciudadano")
                val patternIdentificadorC: Pattern = Pattern.compile(".<<(.*?)MEX<.")

                val matcherIdentificadorC = patternIdentificadorC.matcher(stringDetetions)

                if (matcherIdentificadorC.find()) {
                    if (matcherIdentificadorC.group(1)?.uppercase(Locale.getDefault())!= null) {
                        val value = matcherIdentificadorC.group(1)
                            ?.uppercase(Locale.getDefault())!!
                            .replace("\\s+","")
                            .substring(4..12)
                        if(pattern.matcher(value).find()) _liveIdentificadorC.postValue(value)
                        else _liveIdentificadorC.postValue("")
                    } else _liveIdentificadorC.postValue("")
                } else _liveIdentificadorC.postValue("")
            }
        }
    }

    fun getInfoINE(): InfoINEReverso {
        return InfoINEReverso(
            _liveCic.value.toString(),
            _liveIdentificadorC.value.toString()
        )
    }

    fun isEsMuCoScanned(): Boolean {
        return !_liveCic.value.equals("") && !_liveIdentificadorC.value.equals("")
    }

    //SET VALUES
    fun setValueCic(value: String) {
        if (value.isNotEmpty()) {
            _liveCic.postValue(value)
        }
    }

    fun setValueIdentificadorC(value: String) {
        if (value.isNotEmpty()) {
            _liveIdentificadorC.postValue(value)
        }
    }

    @Synchronized
    fun disminuirContador() {
        _liveTotalCampos.value = _liveTotalCampos.value!! - 1
    }

    @Synchronized
    fun aumentarContador() {
        _liveTotalCampos.value = _liveTotalCampos.value!! + 1
    }

    override fun onCleared() {
        super.onCleared()
        Utils.freeMemory()
        println("Termino el LifeCicle del ActOCRINEREVERSO")
    }
}