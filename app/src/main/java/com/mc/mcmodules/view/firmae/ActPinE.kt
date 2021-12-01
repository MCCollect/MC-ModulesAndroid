package com.mc.mcmodules.view.firmae


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.mc.mcmodules.databinding.ActPinEBinding
import com.mc.mcmodules.model.classes.data.DataInfoAutorizacion
import com.mc.mcmodules.utils.Utils
import com.mc.mcmodules.viewmodel.firmae.PinEViewmodel


class ActPinE : AppCompatActivity() {

    companion object {
        const val CODIGO_OK_E_DATA: Int = 601
    }


    private lateinit var selfViewModel: PinEViewmodel
    private lateinit var binding: ActPinEBinding
    private lateinit var dataautorizacion: DataInfoAutorizacion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recoverDataIntent()
        initContentView()
        initViews()

    }


    private fun initContentView() {
        selfViewModel = ViewModelProvider(this).get(PinEViewmodel::class.java)
        binding = ActPinEBinding.inflate(layoutInflater)
        binding.mainView = this
        binding.viewModel = selfViewModel
        binding.lifecycleOwner = this
        val view = binding.root
        setContentView(view)
    }

    private fun initViews() {

        with(binding) {

            val ft: FragmentTransaction =
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)

            ft.commitAllowingStateLoss()
            ft.replace(
                contentFrg.id,
                FrgAutorizacionE(dataautorizacion)
            )


        }
    }


    private fun recoverDataIntent() {
        val intent = intent

        if (intent == null) {


            throw java.lang.Exception("Los datos de la verificación son obligatorios (2)")

        } else {
            val dataautorizacion =
                intent.getParcelableExtra<DataInfoAutorizacion>("data_autorizacion")

            if (dataautorizacion == null) {
                throw java.lang.Exception("Los datos de la verificación son obligatorios")
            } else {
                if (Utils.checkURL(dataautorizacion.URL_BASE)) {
                    this.dataautorizacion = dataautorizacion
                } else {
                    throw java.lang.Exception("La URL BASE no es válida")
                }
            }


        }


    }





}