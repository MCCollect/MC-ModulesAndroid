package com.mc.mcmodules.view.pinhc.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.ActPinHcBinding
import com.mc.mcmodules.model.classes.DataInfoAutorizacion
import com.mc.mcmodules.model.classes.DataInfoDomicilio
import com.mc.mcmodules.model.classes.DataInfoSolicitante
import com.mc.mcmodules.view.adapters.pinhc.PagerFragmentAdapter
import com.mc.mcmodules.viewmodel.pinhc.PinHCViewmodel


class ActPinHC : AppCompatActivity() {


    private lateinit var selfViewModel: PinHCViewmodel
    private lateinit var binding: ActPinHcBinding

    private lateinit var datasolicitante: DataInfoSolicitante
    private lateinit var datadomicilio: DataInfoDomicilio
    private lateinit var dataautorizacion: DataInfoAutorizacion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recoverDataIntent()
        initContentView()
        initViews()
        initListeners()

    }


    private fun initContentView() {
        selfViewModel = ViewModelProvider(this).get(PinHCViewmodel::class.java)
        binding = ActPinHcBinding.inflate(layoutInflater)
        binding.mainView = this
        binding.viewModel = selfViewModel
        binding.lifecycleOwner = this
        val view = binding.root
        setContentView(view)
    }

    private fun initViews() {

        with(binding) {

            stepsView.currentStep = 0
            viewPager.adapter = PagerFragmentAdapter(
                this@ActPinHC,
                viewPager,
                stepsView,
                datasolicitante,
                datadomicilio,
                dataautorizacion

            )
            viewPager.isUserInputEnabled = false

        }
    }

    private fun initListeners() {
        with(binding) {
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            txtPasoActual.text = resources.getString(R.string.datos_solicitante)
                        }
                        1 -> {
                            txtPasoActual.text = resources.getString(R.string.domicilio2)
                        }
                        2 -> {
                            txtPasoActual.text =
                                resources.getString(R.string.autorizacion_y_consultas)
                        }
                    }
                    super.onPageSelected(position)
                }
            })
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
                this.dataautorizacion = dataautorizacion
                setDataIntent()
            }


        }


    }


    private fun setDataIntent() {

        val datasolicitante = intent.getParcelableExtra<DataInfoSolicitante>("data_solicitante")

        if (datasolicitante != null) {

            this.datasolicitante = datasolicitante

        } else {
            this.datasolicitante = DataInfoSolicitante(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                arrayListOf(true,true,true,true,true,true,true)
            )
        }


        val datadomicilio = intent.getParcelableExtra<DataInfoDomicilio>("data_domicilio")

        if (datadomicilio != null) {

            this.datadomicilio = datadomicilio

        } else {
            this.datadomicilio = DataInfoDomicilio(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                arrayListOf(true,true,true,true,true,true,true,true,true)
            )
        }


    }
}