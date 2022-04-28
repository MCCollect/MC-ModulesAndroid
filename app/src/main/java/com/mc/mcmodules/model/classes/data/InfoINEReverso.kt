package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class InfoINEReverso(
    var cic: String,
    var identificadorC: String,
) : Parcelable {
    fun printInfo() = "CIC = $cic\n" + "IDENTIFICADOR = $identificadorC\n"
}