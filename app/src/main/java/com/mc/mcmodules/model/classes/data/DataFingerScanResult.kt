package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 1.- El parámetro
 * @param results es un arreglo de instancias de la clase DataFingerScanned en la cual se encuentran
 * los resultados de las capturas de las huellas digitales, en su version más simple el arreglo
 * contendrá por lo menos 1 objeto contenido.
 * **/
@Parcelize
class DataFingerScanResult(
    var results: ArrayList<DataFingerScanned>
) : Parcelable {


    /**
     * @param pathResult ruta de la imagen con la huella capturada.
     * @param calidad calidad de la huella capturada con valo del 0 al 1 a dos digitos (limitado
     * manualmente).
     * @param dedo nombre del dedo que fue capturado.
     *
     * **/
    @Parcelize
    class DataFingerScanned(
        var pathResult: String,
        var calidad: Double,
        var dedo:String
    ) : Parcelable

}