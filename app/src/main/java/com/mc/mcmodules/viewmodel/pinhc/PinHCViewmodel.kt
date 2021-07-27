package com.mc.mcmodules.viewmodel.pinhc

import androidx.lifecycle.ViewModel

class PinHCViewmodel:ViewModel() {





    override fun onCleared() {
        super.onCleared()
        println("Termino el LifeCicle del ActPinHC")
    }

}