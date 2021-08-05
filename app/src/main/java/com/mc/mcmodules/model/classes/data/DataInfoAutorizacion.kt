package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class DataInfoAutorizacion(
    var TEXT1_CHECKBOX: String,
    var TEXT2_TITLE: String,
    var TEXT3_PARRAFO1: String,
    var TEXT4_PARRAFO2: String,
    var URL_CONDICIONES: String,
    var URL_NIP: String,
    var URL_BASE: String,
    var USUARIO: String,
    var INTEGRANTE: String,
    var PATH_FIRMA: String
) : Parcelable