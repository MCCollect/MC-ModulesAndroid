package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class DataInfoSolicitante(
    var NOMBRES: String,
    var PATERNO: String,
    var MATERNO: String,
    var SEXO: String,
    var ESTADO_CIVIL: String,
    var CURP: String,
    var FECHA_NAC: String,
    var LOCK_FIELDS: ArrayList<Boolean>
) : Parcelable