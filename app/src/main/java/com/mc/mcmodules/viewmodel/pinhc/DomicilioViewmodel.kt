package com.mc.mcmodules.viewmodel.pinhc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mc.mcmodules.model.classes.DataInfoDomicilio

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
            ""
        )
    }

    fun setDataDomicilio(dataInfoSolicitante: DataInfoDomicilio) {
        _liveDatosDomicilio.postValue(dataInfoSolicitante)
    }




    fun setInstanceCompanionViewModel(domicilioViewmodel: DomicilioViewmodel) {
        DomicilioViewmodel.domicilioViewmodel = domicilioViewmodel
    }





    override fun onCleared() {
        super.onCleared()
        domicilioViewmodel = null
        println("Termino el lifeCicle de FrgDomicilio ")
    }
}