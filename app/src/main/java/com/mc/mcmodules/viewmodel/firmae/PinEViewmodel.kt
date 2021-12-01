package com.mc.mcmodules.viewmodel.firmae

import androidx.lifecycle.ViewModel
import com.mc.mcmodules.utils.Utils

class PinEViewmodel:ViewModel() {





    override fun onCleared() {
        super.onCleared()
        Utils.freeMemory()
        println("Termino el LifeCicle del ActPinHC")
    }

}