package com.mc.mcmodules.viewmodel.pinhc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mc.mcmodules.model.classes.DataInfoAutorizacion

class AutorizacionViewmodel:ViewModel() {

    companion object {
        private var autorizacionViewmodel: AutorizacionViewmodel? = null

        @Synchronized
        fun getInstance(): AutorizacionViewmodel? {

            if (autorizacionViewmodel == null) {
                throw java.lang.Exception("No hay instancia creada de este viewModel(AutorizacionViewmodel)")

            }
            return autorizacionViewmodel
        }

    }

    private var _liveDatosAutorizacion: MutableLiveData<DataInfoAutorizacion> = MutableLiveData()
    val liveDatosAutorizacion: LiveData<DataInfoAutorizacion> get() = _liveDatosAutorizacion

    init {
        _liveDatosAutorizacion.value = DataInfoAutorizacion(
            "",
            "",
            "",
            "",
            "",
            ""
        )
    }

    fun setDataAutorizacion(dataInfoAutorizacion: DataInfoAutorizacion) {
        _liveDatosAutorizacion.postValue(dataInfoAutorizacion)
    }




    fun setInstanceCompanionViewModel(autorizacionViewmodel: AutorizacionViewmodel) {
        AutorizacionViewmodel.autorizacionViewmodel = autorizacionViewmodel
    }




    override fun onCleared() {
        super.onCleared()
        autorizacionViewmodel = null
        println("Termino el lifeCicle de FrgAutorizacion ")
    }



}