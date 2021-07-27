package com.mc.mcmodules.viewmodel.pinhc

import androidx.lifecycle.ViewModel
import com.mc.mcmodules.utils.Utils

class PinHCViewmodel:ViewModel() {





    override fun onCleared() {
        super.onCleared()
        Utils.freeMemory()
        println("Termino el LifeCicle del ActPinHC")
    }

}