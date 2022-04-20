package com.mc.mcmodules.model.classes.data

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
    var RFC: String,
    var EMISION: String,
    var CLAVE_ELECTOR: String,
    var VIGENCIA: String
) : Parcelable {

    fun printInfo() =
        "NOMBRE = $NOMBRE\n" +
        "DOMICILO = $DOMICILO\n" +
        "CURP = $CURP\n" +
        "ESTADO = $ESTADO\n" +
        "MUNICIPIO = $MUNICIPIO\n" +
        "COLONIA = $COLONIA\n" +
        "SEXO = $SEXO\n" +
        "RFC = $RFC\n" +
        "EMISION = $EMISION\n" +
        "CLAVE_ELECTOR = $CLAVE_ELECTOR\n" +
        "VIGENCIA = $VIGENCIA"
}