package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DataInfoDomicilio(
    var CALLE: String,
    var NO_INT: String,
    var NO_EXT: String,
    var CODIGO_POSTAL: String,
    var TEL_CELULAR: String,
    var COLONIA: String,
    var MUNICIPIO: String,
    var CIUDAD: String,
    var ESTADO: String,
    var LOCK_FIELDS: ArrayList<Boolean>
) : Parcelable