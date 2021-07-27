package com.mc.mcmodules.viewmodel.pinhc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mc.mcmodules.model.classes.data.DataInfoSolicitante
import com.mc.mcmodules.utils.Utils
import java.lang.StringBuilder

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
            arrayListOf(true, true, true, true, true, true, true)
        )
    }

    fun setDataSolicitante(dataInfoSolicitante: DataInfoSolicitante) {
        _liveDatosSolicitante.postValue(dataInfoSolicitante)
    }


    fun setInstanceCompanionViewModel(datosSolicitanteViewmodel: DatosSolicitanteViewmodel) {
        DatosSolicitanteViewmodel.datosSolicitanteViewmodel = datosSolicitanteViewmodel
    }


    fun getDataStringSolicitante(): String {

        val strBuilder = StringBuilder()
        val delimiter = "~"

        strBuilder.append(_liveDatosSolicitante.value?.NOMBRES ?: "ErrorNombre").append(delimiter)
        strBuilder.append(_liveDatosSolicitante.value?.PATERNO ?: "ErrorPaterno").append(delimiter)
        strBuilder.append(_liveDatosSolicitante.value?.MATERNO ?: "ErrorMaterno").append(delimiter)
        strBuilder.append(_liveDatosSolicitante.value?.SEXO ?: "ErrorSexo").append(delimiter)
        strBuilder.append(_liveDatosSolicitante.value?.ESTADO_CIVIL ?: "ErrorEC").append(delimiter)
        strBuilder.append(_liveDatosSolicitante.value?.CURP ?: "ErrorCURP").append(delimiter)
        strBuilder.append(_liveDatosSolicitante.value?.FECHA_NAC ?: "ErrorFecNac")


        return strBuilder.toString()


    }


    override fun onCleared() {
        super.onCleared()
        datosSolicitanteViewmodel = null
        Utils.freeMemory()
        println("Termino el lifeCicle de FrgDatosSolicitante ")
    }


}