package com.mc.mcmodules.viewmodel.firmae

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mc.mcmodules.model.classes.data.DataInfoAutorizacion
import com.mc.mcmodules.model.classes.library.CustomAlert
import com.mc.mcmodules.model.classes.webmodels.PostPIN
import com.mc.mcmodules.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class AutorizacionEViewmodel : ViewModel() {

    companion object {
        private var autorizacionViewmodel: AutorizacionEViewmodel? = null

        @Synchronized
        fun getInstance(): AutorizacionEViewmodel? {

            if (autorizacionViewmodel == null) {
                throw java.lang.Exception("No hay instancia creada de este viewModel(AutorizacionEViewmodel)")

            }
            return autorizacionViewmodel
        }

    }

    interface APIInterface {
        @POST("{api}")
        fun postPIN(@Body wsPin: PostPIN, @Path("api") api: String): Call<PostPIN>
    }


    private var okHttpClient: OkHttpClient

    private var _liveDatosAutorizacion: MutableLiveData<DataInfoAutorizacion> = MutableLiveData()
    val liveDatosAutorizacion: LiveData<DataInfoAutorizacion> get() = _liveDatosAutorizacion

    private var _liveRequestService: MutableLiveData<String> = MutableLiveData()
    val liveRequestService: LiveData<String> get() = _liveRequestService

    var pin: Int = 1234

    init {
        _liveDatosAutorizacion.value = DataInfoAutorizacion(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )

        _liveRequestService.value = "N/C"


        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
        var sslContext: SSLContext? = null
        try {
            sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        val sslSocketFactory = sslContext!!.socketFactory

        val client = OkHttpClient.Builder().addInterceptor(interceptor)
        client.connectTimeout(100, TimeUnit.SECONDS)
        client.readTimeout(100, TimeUnit.SECONDS)
        client.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        client.hostnameVerifier { _, _ -> true }

        okHttpClient = client.build()


    }

    fun setDataAutorizacion(dataInfoAutorizacion: DataInfoAutorizacion) {
        _liveDatosAutorizacion.postValue(dataInfoAutorizacion)
    }


    fun setInstanceCompanionViewModel(autorizacionViewmodel: AutorizacionEViewmodel) {
        Companion.autorizacionViewmodel = autorizacionViewmodel
    }

    fun postNip(postPIN: PostPIN, alert: CustomAlert) {

        viewModelScope.launch(Dispatchers.IO) {

            val api = Retrofit.Builder()
                .baseUrl(_liveDatosAutorizacion.value?.URL_BASE ?: "")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()


            api.create(APIInterface::class.java)
                .postPIN(postPIN, _liveDatosAutorizacion.value?.URL_NIP ?: "").enqueue(object :
                    Callback<PostPIN?> {
                    override fun onResponse(
                        call: Call<PostPIN?>,
                        response: Response<PostPIN?>
                    ) {
                        if (response.isSuccessful) {
                            println("Respondio bien el servicio de pin")

                            val postNIP = response.body()


                            if (postNIP?.Informacion?.size ?: 0 > 0) {

                                pin = postNIP?.Informacion?.get(0)?.Pin ?: 1234
                                viewModelScope.launch(Dispatchers.Main) {
                                    alert.setCancelable(true)
                                    alert.btnLeft.visibility = View.VISIBLE
                                    alert.setTypeSuccess(
                                        "¡Listo!",
                                        "Se ha enviado un SMS con tu NIP",
                                        "Aceptar"
                                    )
                                    alert.btnLeft.setOnClickListener {
                                        alert.close()
                                    }

                                    _liveRequestService.postValue("OK")
                                }

                            } else {

                                println("El servidor ha mandado un error!")

                                viewModelScope.launch(Dispatchers.Main) {
                                    alert.setCancelable(true)
                                    alert.btnLeft.visibility = View.VISIBLE
                                    alert.setTypeError(
                                        "¡Upss!",
                                        "Ocurrio un error al enviar el NIP, intentalo de nuevo",
                                        "Aceptar"
                                    )
                                    alert.btnLeft.setOnClickListener {
                                        alert.close()
                                    }
                                    _liveRequestService.postValue("ERROR")
                                }

                            }


                        } else {
                            println("El servidor ha mandado un error!")

                            viewModelScope.launch(Dispatchers.Main) {
                                alert.setCancelable(true)
                                alert.btnLeft.visibility = View.VISIBLE
                                alert.setTypeError(
                                    "¡Upss!",
                                    "Ocurrio un error al enviar el NIP, intentalo de nuevo",
                                    "Aceptar"
                                )
                                alert.btnLeft.setOnClickListener {
                                    alert.close()
                                }
                                _liveRequestService.postValue("ERROR")
                            }


                        }
                        Utils.freeMemory()
                    }

                    override fun onFailure(call: Call<PostPIN?>, t: Throwable) {
                        if (call.isCanceled) {
                            println("El servidor ha mandado un error!")
                        } else {
                            t.printStackTrace()
                            call.cancel()
                            println("El servidor ha mandado un error!")
                        }
                        viewModelScope.launch(Dispatchers.Main) {
                            alert.setCancelable(true)
                            alert.btnLeft.visibility = View.VISIBLE
                            alert.setTypeError(
                                "¡Upss!",
                                "Ocurrio un error al enviar el NIP, intentalo de nuevo",
                                "Aceptar"
                            )
                            alert.btnLeft.setOnClickListener {
                                alert.close()
                            }
                        }
                        _liveRequestService.postValue("ERROR")

                        Utils.freeMemory()
                    }
                })

        }


    }


    override fun onCleared() {
        super.onCleared()
        autorizacionViewmodel = null
        Utils.freeMemory()
        println("Termino el lifeCicle de FrgAutorizacion ")
    }


}