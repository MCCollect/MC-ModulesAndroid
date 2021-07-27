package com.mc.mcmodules.viewmodel.pinhc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mc.mcmodules.model.classes.DataInfoSolicitante
import com.mc.mcmodules.utils.Utils

class DatosSolicitanteViewmodel : ViewModel() {

    companion object {
        private var datosSolicitanteViewmodel: DatosSolicitanteViewmodel? = null

        @Synchronized
        fun getInstance(): DatosSolicitanteViewmodel? {

            if (datosSolicitanteViewmodel == null) {
                throw java.lang.Exception("No hay instancia creada de este viewModel(DatosSolicitanteViewmodel)")

            }
            return datosSolicitanteViewmodel
        }

    }


    private var _liveDatosSolicitante: MutableLiveData<DataInfoSolicitante> = MutableLiveData()
    val liveDatosSolicitante: LiveData<DataInfoSolicitante> get() = _liveDatosSolicitante

    init {
        _liveDatosSolicitante.value = DataInfoSolicitante(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            arrayListOf(true,true,true,true,true,true,true)
        )
    }

    fun setDataSolicitante(dataInfoSolicitante: DataInfoSolicitante) {
        _liveDatosSolicitante.postValue(dataInfoSolicitante)
    }


    fun setInstanceCompanionViewModel(datosSolicitanteViewmodel: DatosSolicitanteViewmodel) {
        DatosSolicitanteViewmodel.datosSolicitanteViewmodel = datosSolicitanteViewmodel
    }


    override fun onCleared() {
        super.onCleared()
        datosSolicitanteViewmodel = null
        Utils.freeMemory()
        println("Termino el lifeCicle de FrgDatosSolicitante ")
    }


}