package com.mc.mcmodules.view.pinhc.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.badoualy.stepperindicator.StepperIndicator
import com.mc.alternativasur.api.interfaces.ActivityResultHandler
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.FrgAutorizacionBinding
import com.mc.mcmodules.model.classes.data.DataInfoAutorizacion
import com.mc.mcmodules.model.classes.data.PINRequest
import com.mc.mcmodules.model.classes.library.CustomAlert
import com.mc.mcmodules.model.classes.webmodels.PostPIN
import com.mc.mcmodules.model.interfaces.OnSignedCaptureListener
import com.mc.mcmodules.utils.Utils
import com.mc.mcmodules.view.pinhc.activity.ActLoadURL
import com.mc.mcmodules.viewmodel.pinhc.AutorizacionViewmodel
import com.mc.mcmodules.viewmodel.pinhc.DatosSolicitanteViewmodel
import com.mc.mcmodules.viewmodel.pinhc.DomicilioViewmodel
import com.tapadoo.alerter.Alerter
import java.io.FileOutputStream

class FrgAutorizacion(
    private val viewPager: ViewPager2,
    private val stepsView: StepperIndicator,
    private val dataInfoAutorizacion: DataInfoAutorizacion
) : Fragment(), ActivityResultHandler, OnSignedCaptureListener {

    private lateinit var selfViewModel: AutorizacionViewmodel
    private lateinit var binding: FrgAutorizacionBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return initContentView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        setDataAutorizacion()
        initObservers()
    }


    private fun initContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        selfViewModel = ViewModelProvider(this).get(AutorizacionViewmodel::class.java)
        selfViewModel.setInstanceCompanionViewModel(selfViewModel)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.frg_autorizacion, container, false)
        binding.lifecycleOwner = this
        binding.mainView = this
        // binding.HtmlTools = Html()
        binding.viewModel = selfViewModel

        return binding.root
    }

    private fun initListeners() {
        with(binding) {

            btnCaptureFirma.setOnClickListener {
                showDialog()
            }

            btnAceptarFirma.setOnClickListener {
                println("acepto por firma")
                handleResult(PINRequest(-1000, "OK",dataInfoAutorizacion.PATH_FIRMA))
            }


            txtAcepterminos.setOnClickListener {
                val intent = Intent(requireContext(), ActLoadURL::class.java)
                intent.putExtra(
                    "url_load",
                    selfViewModel.liveDatosAutorizacion.value?.URL_CONDICIONES ?: "google.com.mx"
                )
                startActivity(intent)
            }

            prev.setOnClickListener {
                viewPager.setCurrentItem(1, true)
                it.postDelayed({
                    stepsView.currentStep = 1
                }, 300)

            }

            btnAutorizo.setOnClickListener {


                if (cbAceptTerminos.isChecked) {
                    cbAceptTerminos.isEnabled = false
                    postNip()
                } else {

                    showAlerter(
                        R.drawable.ic_close,
                        "No has aceptado los términos",
                        "acepta los terminos para poder continuar",
                        R.color.error
                    )

                }
            }

            btnValidarNIP.setOnClickListener {
                if (binding.etNIP.text.toString().length == 4) {

                    if (binding.etNIP.text.toString() == selfViewModel.pin.toString()) {

                        handleResult(PINRequest(selfViewModel.pin, "OK",""))

                    } else {
                        showAlerter(
                            R.drawable.ic_close,
                            "NIP incorrecto",
                            "Ingresa el NIP que te llego por mensaje",
                            R.color.error

                        )
                    }


                } else {
                    showAlerter(
                        R.drawable.ic_warning,
                        "NIP inválido",
                        "Ingresa un NIP válido para poder continuar",
                        R.color.yellow
                    )

                }
            }


        }

    }

    private fun showAlerter(icon: Int, title: String, text: String, @ColorRes color: Int) {

        ContextCompat.getDrawable(requireContext(), icon)?.let { it1 ->
            Alerter.create(requireActivity())
                .setTitle(title)
                .setText(text)
                .setIcon(it1)
                .setBackgroundColorRes(color)
                .setDuration(2500)
                .show()
        }

    }

    private fun postNip() {
        val alert = CustomAlert(requireActivity())

        alert.setTypeWarning(
            "¿Enviar mensaje?",
            "Se enviara un mensaje con tu NIP al celular ${DomicilioViewmodel.getInstance()?.liveDatosDomicilio?.value?.TEL_CELULAR ?: "5555555555"} ",
            "Cancelar",
            "Aceptar"
        )
        alert.btnLeft.setOnClickListener {
            alert.close()
        }
        alert.btnRight.setOnClickListener {
            alert.setTypeProgress("Enviando NIP...", "", "")
            alert.setCancelable(false)
            alert.btnLeft.visibility = View.GONE
            selfViewModel.postNip(
                PostPIN(
                    "0",
                    selfViewModel.liveDatosAutorizacion.value?.USUARIO
                        ?: "No se pudo obtener el usuario",
                    selfViewModel.liveDatosAutorizacion.value?.INTEGRANTE
                        ?: "No se pudo obtener el integrante",
                    selfViewModel.pin.toString(),
                    DatosSolicitanteViewmodel.getInstance()?.getDataStringSolicitante()
                        ?: "No se pudo obtener datos solicitante",
                    DomicilioViewmodel.getInstance()?.getDataStringDomicilio()
                        ?: "No se pudo obtener datos domicilio"
                ), alert
            )
        }
        requireActivity().runOnUiThread {
            alert.show()
        }

    }

    private fun setDataAutorizacion() {
        selfViewModel.setDataAutorizacion(dataInfoAutorizacion)
    }

    private fun initObservers() {

        selfViewModel.liveDatosAutorizacion.observe(requireActivity(), { dataInfoAutorizacion ->


            with(binding) {

                txtAcepterminos.text = Utils.fromHtml(dataInfoAutorizacion.TEXT1_CHECKBOX)
                text1.text = Utils.fromHtml(dataInfoAutorizacion.TEXT2_TITLE)
                text2.text = Utils.fromHtml(dataInfoAutorizacion.TEXT3_PARRAFO1)
                text3.text = Utils.fromHtml(dataInfoAutorizacion.TEXT4_PARRAFO2)


            }


        })


        selfViewModel.liveRequestService.observe(requireActivity(), { request ->

            if (request != "N/C") {

                if (request.uppercase() == "OK") {
                    binding.btnValidarNIP.visibility = View.VISIBLE
                } else {
                    binding.btnValidarNIP.visibility = View.GONE
                }

            }

        })

    }


    private fun showDialog() {
        val dialogFragment = SignatureDialogFragment(this)
        dialogFragment.show(requireActivity().supportFragmentManager, "signature")
    }

    override fun handleResult(parcelable: Parcelable) {

        val intentRegreso = Intent()
        intentRegreso.putExtra("result_object", parcelable)
        requireActivity().setResult(AppCompatActivity.RESULT_OK, intentRegreso)
        requireActivity().finish()
    }

    override fun onSignatureCaptured(bitmap: Bitmap, fileUri: String) {
        binding.imgFirma.setImageBitmap(null)
        binding.imgFirma.setImageBitmap(bitmap)
        binding.imgFirma.visibility = View.VISIBLE
        binding.btnAceptarFirma.visibility = View.VISIBLE

        println("PathFirma:${dataInfoAutorizacion.PATH_FIRMA}")

        FileOutputStream(this.dataInfoAutorizacion.PATH_FIRMA).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }


    }


}