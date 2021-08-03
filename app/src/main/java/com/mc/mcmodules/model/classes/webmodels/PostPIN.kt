package com.mc.mcmodules.model.classes.webmodels

data class PostPIN(
    var V_Bandera: String,
    var V_Usuario:String,
    var V_Integrante:String,
    var V_PIN:String,
    var V_Datos_Soli :String,
    var V_Datos_Domi :String,
){

    lateinit var Estatus: String
    lateinit var Informacion: ArrayList<InfoObject>
}
