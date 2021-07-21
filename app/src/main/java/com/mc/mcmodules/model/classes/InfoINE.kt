package com.mc.mcmodules.model.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class InfoINE(
    var NOMBRE: String,
    var DOMICILO: String,
    var CURP: String,
    var ESTADO: String,
    var MUNICIPIO: String,
    var COLONIA: String,
    var SEXO: String,
    var RFC: String
) : Parcelable