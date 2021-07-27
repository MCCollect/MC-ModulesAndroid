package com.mc.mcmodules.viewmodel.scanine

import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.vision.text.TextBlock
import com.mc.mcmodules.model.classes.InfoINE
import com.mc.mcmodules.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class OCRINEViewmodel : ViewModel() {

    /***
     * Campos -> valores estaticos finales que sirven para saber que campo se necesita modificar
     * */

    companion object {
        const val FIELD_NOMBRE = 1
        const val FIELD_DOMICILIO = 2
        const val FIELD_FECHA = 3
        const val FIELD_EDAD = 4
        const val FIELD_SEXO = 5
        const val FIELD_CLAVE_ELECTOR = 6
        const val FIELD_CURP = 7
        const val FIELD_EMISION = 8
        const val FIELD_TIPO_INE = 9
        const val FIELD_ESTADO = 10
        const val FIELD_MUNICIPIO = 11
        const val FIELD_COLONIA = 12

    }


    private var _liveNombre: MutableLiveData<String> = MutableLiveData()
    val liveNombre: LiveData<String> get() = _liveNombre

    private var _liveDomicilio: MutableLiveData<String> = MutableLiveData()
    val liveDomicilio: LiveData<String> get() = _liveDomicilio

    private var _liveFecha: MutableLiveData<String> = MutableLiveData()
    val liveFecha: LiveData<String> get() = _liveFecha

    private var _liveEdad: MutableLiveData<String> = MutableLiveData()
    val liveEdad: LiveData<String> get() = _liveEdad

    private var _liveSexo: MutableLiveData<String> = MutableLiveData()
    val livesexo: LiveData<String> get() = _liveSexo

    private var _liveClaveLector: MutableLiveData<String> = MutableLiveData()
    val liveClaveLector: LiveData<String> get() = _liveClaveLector

    private var _liveCurp: MutableLiveData<String> = MutableLiveData()
    val liveCurp: LiveData<String> get() = _liveCurp

    /*private var _liveEmision: MutableLiveData<String> = MutableLiveData()
    val liveEmision: LiveData<String> get() = _liveEmision

    private var _liveTipoIne: MutableLiveData<String> = MutableLiveData()
    val liveTipoIne: LiveData<String> get() = _liveTipoIne*/

    private var _liveEstado: MutableLiveData<String> = MutableLiveData()
    val liveEstado: LiveData<String> get() = _liveEstado

    private var _liveMunicipio: MutableLiveData<String> = MutableLiveData()
    val liveMunicipio: LiveData<String> get() = _liveMunicipio


    private var _liveColonia: MutableLiveData<String> = MutableLiveData()
    val liveColonia: LiveData<String> get() = _liveColonia


    private var _liveTotalCampos: MutableLiveData<Int> = MutableLiveData()
    val liveTotalCampos: LiveData<Int> get() = _liveTotalCampos

    init {
        _liveNombre.value = ""
        _liveDomicilio.value = ""
        _liveFecha.value = ""
        _liveEdad.value = ""
        _liveSexo.value = ""
        _liveClaveLector.value = ""
        _liveCurp.value = ""
        /*_liveEmision.value = ""
        _liveTipoIne.value = ""*/
        _liveTotalCampos.value = 0
        _liveEstado.value = ""
        _liveMunicipio.value = ""
        _liveColonia.value = ""
    }


    fun updateCampo(field: Int, value: String) {

        viewModelScope.launch(Dispatchers.IO) {
            when (field) {
                FIELD_NOMBRE -> {
                    _liveNombre.postValue(value)
                }

                FIELD_DOMICILIO -> {
                    _liveDomicilio.postValue(value)
                }

                FIELD_FECHA -> {
                    _liveFecha.postValue(value)
                    _liveEdad.postValue(value)
                }

                FIELD_SEXO -> {
                    _liveSexo.postValue(value)
                }

                FIELD_CLAVE_ELECTOR -> {
                    _liveClaveLector.postValue(value)
                }

                FIELD_CURP -> {
                    _liveCurp.postValue(value)
                }
/*
                FIELD_EMISION -> {
                    _liveEmision.postValue(value)
                }

                FIELD_TIPO_INE -> {
                    _liveTipoIne.postValue(value)
                }*/

                FIELD_ESTADO -> {
                    _liveEstado.postValue(value)
                }

                FIELD_MUNICIPIO -> {
                    _liveMunicipio.postValue(value)
                }

                FIELD_COLONIA -> {
                    _liveColonia.postValue(value)
                }
            }

        }


    }

    fun startThreadOCR(items: SparseArray<TextBlock?>) {


        viewModelScope.launch(Dispatchers.IO) {

            val stringBuilder = StringBuilder()
            for (i in 0 until items.size()) {
                val item = items.valueAt(i)
                stringBuilder.append(item?.value)
                stringBuilder.append("\n")
            }


            val stringDetetions = stringBuilder.toString().replace("\n", " ")
                .uppercase(Locale.getDefault())

            println("Detectios $stringDetetions")

            if (_liveNombre.value.equals("")) {
                println("Buscando nombre")
                val patternNombre = Pattern.compile(".*NOMBRE(.*?)FECHA DE NACIMIENTO.")

                val matcherNombre =
                    patternNombre.matcher(stringDetetions)
                if (matcherNombre.find()) {


                    if ((matcherNombre.group(1)?.trim()?.split(" ")?.size ?: 0) in 3..5) {
                        _liveNombre.postValue(
                            matcherNombre.group(1)?.uppercase(Locale.getDefault())
                        )
                    }


                } else {
                    val patternNombre2 = Pattern.compile(".*NOMBRE(.*?)EDAD.")
                    val matcherNombre2 = patternNombre2.matcher(stringDetetions)
                    if (matcherNombre2.find()) {

                        if ((matcherNombre2.group(1)?.trim()?.split(" ")?.size ?: 0) in 3..5) {
                            _liveNombre.postValue(
                                matcherNombre2.group(1)?.uppercase(Locale.getDefault())
                            )
                        }


                    } else {
                        val patternNombre3 = Pattern.compile(".*NOMBRE(.*?)DOMICILIO.")
                        val matcherNombre3 = patternNombre3.matcher(stringDetetions)
                        if (matcherNombre3.find()) {

                            if ((matcherNombre3.group(1)?.trim()?.split(" ")?.size
                                    ?: 0) in 3..5
                            ) {
                                _liveNombre.postValue(
                                    matcherNombre3.group(1)?.uppercase(Locale.getDefault())
                                )
                            }


                        } else {
                            _liveNombre.postValue("")
                        }
                    }
                }


            }

            if (_liveDomicilio.value.equals("")) {
                println("Buscando domicilio")


                val patternDomicilio: Pattern =
                    Pattern.compile(".DOMICILIO(.*?)\\s*(CLAVE DE ELECTOR|FOLIO).")

                val matcherDomicilio = patternDomicilio.matcher(stringDetetions)

                if (matcherDomicilio.find()) {
                    _liveDomicilio.postValue(
                        matcherDomicilio.group(1)?.uppercase(Locale.getDefault())
                    )
                } else {
                    _liveDomicilio.postValue("")
                }


            }



            if (_liveFecha.value.equals("")) {
                println("Buscando fecha")


                val patternFecha = Pattern.compile(".CURP\\s*\\w{4}(\\d{6})\\w{6}\\d{2}")


                val matcherNacimiento = patternFecha.matcher(stringDetetions)

                if (matcherNacimiento.find()) {
                    val fechaBruta = matcherNacimiento.group(1)
                    println("fechaBruta :$fechaBruta")
                    var fechaFinal = ""
                    val format = SimpleDateFormat("yyMMdd", Locale.getDefault())
                    var age = 0
                    try {
                        val date = format.parse(fechaBruta!!)
                        println(date)
                        val calendar = Calendar.getInstance()
                        calendar.time = date!!
                        println("MONTH:1 " + calendar[Calendar.MONTH]) //number of seconds
                        fechaFinal += if (calendar[Calendar.DAY_OF_MONTH].toString().length == 1) {
                            "0" + calendar[Calendar.DAY_OF_MONTH] + "/"
                        } else {
                            calendar[Calendar.DAY_OF_MONTH].toString() + "/"
                        }
                        fechaFinal += if ((calendar[Calendar.MONTH] + 1).toString().length == 1) {
                            "0" + (calendar[Calendar.MONTH] + 1) + "/" + calendar[Calendar.YEAR]
                        } else {
                            (calendar[Calendar.MONTH] + 1).toString() + "/" + calendar[Calendar.YEAR]
                        }

                        val dateFormatActual = SimpleDateFormat("yyMMdd", Locale.getDefault())
                        val fechaActual = Date()
                        val fechaNow = dateFormatActual.format(fechaActual)
                        println("ACTUAL : $fechaNow")
                        val dias = ((fechaActual.time - date.time) / 86400000).toInt()
                        println("Diferencia : " + dias / 365)


                        age = dias / 365
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    _liveFecha.postValue(fechaFinal)
                    _liveEdad.postValue(age.toString())

                } else {
                    _liveFecha.postValue("")
                    _liveEdad.postValue("")
                }

            }


            if (_liveSexo.value.equals("")) {
                println("Buscando sexo")


                val patternSexo = Pattern.compile(".CURP\\s*\\w{10}([HM])\\w{5}\\d{2}")


                val matcherSexo =
                    patternSexo.matcher(stringDetetions)

                if (matcherSexo.find()) {
                    _liveSexo.postValue(matcherSexo.group(1)?.uppercase(Locale.getDefault()))
                } else {
                    _liveSexo.postValue("")
                }


            }



            if (_liveClaveLector.value.equals("")) {
                println("Buscando clave de elector")

                val patternClaveElector = Pattern.compile(".CLAVE DE ELECTOR\\s*(\\w{18})")


                val matcherClaveElector = patternClaveElector.matcher(stringDetetions)

                if (matcherClaveElector.find()) {
                    _liveClaveLector.postValue(
                        matcherClaveElector.group(1)?.uppercase(Locale.getDefault())
                    )
                } else {
                    _liveClaveLector.postValue("")
                }


            }

            if (_liveCurp.value.equals("")) {
                println("Buscando curp")

                val patternCurp = Pattern.compile(".CURP\\s*(\\w{16}\\d{2})")

                val matcherCurp =
                    patternCurp.matcher(stringDetetions)

                if (matcherCurp.find()) {

                    _liveCurp.postValue(matcherCurp.group(1)?.uppercase(Locale.getDefault()))
                } else {
                    _liveCurp.postValue("")
                }


            }


            /*if (_liveTipoIne.value.equals("")) {
                println("Buscando tipo ine")


                val patternEmision = Pattern.compile("\\s*REGISTRO\\s*\\d{4}\\s*(\\d{2})")


                val matcherEmision = patternEmision.matcher(stringDetetions)

                if (matcherEmision.find()) {

                    _liveTipoIne.postValue(matcherEmision.group(1)?.uppercase(Locale.getDefault()))
                } else {
                    _liveTipoIne.postValue("")
                }


            }


            if (_liveEmision.value.equals("")) {
                println("Buscando emision")

                val patternTipo = Pattern.compile(".EMISIÓN\\s*(\\d{4})")


                val matcherTipo =
                    patternTipo.matcher(stringDetetions)

                if (matcherTipo.find()) {
                    val typeINE = matcherTipo.group(1)?.uppercase(Locale.getDefault())?.toInt()
                    println(
                        "Progress AnioRegistro: ${
                            matcherTipo.group(1)?.uppercase(Locale.getDefault())
                        }"
                    )
                    println("Tipo de INE $typeINE")

                    _liveEmision.postValue(typeINE.toString())
                } else {
                    _liveEmision.postValue("")
                }

            }*/



            if (_liveEstado.value.equals("")) {
                println("Buscando estado")

                val patternEstado = Pattern.compile(".ESTADO\\s*(\\d{2})")


                val matcherEstado =
                    patternEstado.matcher(stringDetetions)

                if (matcherEstado.find()) {
                    val estado = matcherEstado.group(1)?.uppercase(Locale.getDefault())

                    println("Estado $estado")

                    _liveEstado.postValue(estado)

                } else {
                    _liveEstado.postValue("")
                }

            }


            if (_liveMunicipio.value.equals("")) {
                println("Buscando municipio")

                val patternMunicipio = Pattern.compile(".MUNICIPIO\\s*(\\d{3})")


                val matcherMunicipio =
                    patternMunicipio.matcher(stringDetetions)

                if (matcherMunicipio.find()) {
                    val municipio = matcherMunicipio.group(1)?.uppercase(Locale.getDefault())

                    println("Municipio $municipio")

                    _liveMunicipio.postValue(municipio)
                } else {
                    _liveMunicipio.postValue("")
                }

            }

            if (_liveColonia.value.equals("")) {
                println("Buscando colonia")

                val patternColonia = Pattern.compile(".LOCALIDAD\\s*(\\d{4})")


                val matcherColonia =
                    patternColonia.matcher(stringDetetions)

                if (matcherColonia.find()) {
                    val colonia = matcherColonia.group(1)?.uppercase(Locale.getDefault())

                    println("Colonia $colonia")

                    _liveColonia.postValue(colonia)
                } else {
                    _liveColonia.postValue("")
                }

            }


        }


    }

    fun getInfoINE(): InfoINE {

        return InfoINE(
            _liveNombre.value.toString(),
            _liveDomicilio.value.toString(),
            _liveCurp.value.toString(),
            _liveEstado.value.toString(),
            _liveMunicipio.value.toString(),
            _liveColonia.value.toString(),
            _liveSexo.value.toString(),
            _liveCurp.value.toString().substring(0, 10),
        )

    }

    fun isEsMuCoScanned(): Boolean {

        return !_liveEstado.value.equals("") && !_liveMunicipio.value.equals("") && !_liveColonia.value.equals(
            ""
        )

    }


    fun setValueEstado(estado: String) {

        if (estado.isNotEmpty()) {
            _liveEstado.postValue(estado)
        }
    }

    fun setValueMunicipio(municipio: String) {


        if (municipio.isNotEmpty()) {
            _liveMunicipio.postValue(municipio)
        }

    }

    fun setValueColonia(colonia: String) {

        if (colonia.isNotEmpty()) {

            _liveColonia.postValue(colonia)
        }
    }


    @Synchronized
    fun disminuirContador() {

        _liveTotalCampos.value = _liveTotalCampos.value!! - 1
    }

    @Synchronized
    fun aumentarContador() {


        _liveTotalCampos.value = _liveTotalCampos.value!! + 1
    }

    override fun onCleared() {
        super.onCleared()
        Utils.freeMemory()
        println("Termino el LifeCicle del ActOCRINE")
    }


}