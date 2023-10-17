package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 1.- Solo path del root donde se desea almacenar para
 * @param pathImageIR y
 * @param pathImageIL,
 * el nombre se regresa en automático.
 *
 * 2.- Solo el nombre de la licencia para
 * @param pathLicSDK esta debe estar almacenada en la
 * carpeta assets del proyecto anfitrión
 *
 * **/
@Parcelize
class DataFingerScan(
    var pathImageIR: String,
    var pathImageIL: String,
    var pathLicSDK: String
) : Parcelable