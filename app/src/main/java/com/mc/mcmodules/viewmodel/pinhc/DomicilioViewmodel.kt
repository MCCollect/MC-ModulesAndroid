package com.mc.mcmodules.viewmodel.pinhc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mc.mcmodules.model.classes.data.DataInfoDomicilio
import com.mc.mcmodules.utils.Utils
import java.lang.StringBuilder

class DomicilioViewmodel:ViewModel() {


    companion object {
        private var domicilioViewmodel: DomicilioViewmodel? = null

        @Synchronized
        fun getInstance(): DomicilioViewmodel? {

            if (domicilioViewmodel == null) {
                throw java.lang.Exception("No hay instancia creada de este viewModel(DomicilioViewmodel)")

            }
            return domicilioViewmodel
        }

    }

    private var _liveDatosDomicilio: MutableLiveData<DataInfoDomicilio> = MutableLiveData()
    val liveDatosDomicilio: LiveData<DataInfoDomicilio> get() = _liveDatosDomicilio

    init {
        _liveDatosDomicilio.value = DataInfoDomicilio(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            arrayListOf(true,true,true,true,true,true,true,true,true)
        )
    }

    fun setDataDomicilio(dataInfoSolicitante: DataInfoDomicilio) {
        _liveDatosDomicilio.postValue(dataInfoSolicitante)
    }




    fun setInstanceCompanionViewModel(domicilioViewmodel: DomicilioViewmodel) {
        DomicilioViewmodel.domicilioViewmodel = domicilioViewmodel
    }

    fun getDataStringDomicilio(): String {

        val strBuilder = StringBuilder()
        val delimiter = "~"

        strBuilder.append(_liveDatosDomicilio.value?.CALLE ?: "ErrorCALLE").append(delimiter)
        strBuilder.append(_liveDatosDomicilio.value?.NO_INT ?: "ErrorNOINT").append(delimiter)
        strBuilder.append(_liveDatosDomicilio.value?.NO_EXT ?: "ErrorNOEXT").append(delimiter)
        strBuilder.append(_liveDatosDomicilio.value?.CODIGO_POSTAL ?: "ErrorCP").append(delimiter)
        strBuilder.append(_liveDatosDomicilio.value?.TEL_CELULAR ?: "ErrorTEL").append(delimiter)
        strBuilder.append(_liveDatosDomicilio.value?.COLONIA ?: "ErrorCOLONIA").append(delimiter)
        strBuilder.append(_liveDatosDomicilio.value?.MUNICIPIO ?: "ErrorMUNI").append(delimiter)
        strBuilder.append(_liveDatosDomicilio.value?.CIUDAD ?: "ErrorCIUDAD").append(delimiter)
        strBuilder.append(_liveDatosDomicilio.value?.ESTADO ?: "ErrorESTADO")


        return strBuilder.toString()


    }






    override fun onCleared() {
        super.onCleared()
        domicilioViewmodel = null
        Utils.freeMemory()
        println("Termino el lifeCicle de FrgDomicilio ")
    }
}