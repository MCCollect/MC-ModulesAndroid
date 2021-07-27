package com.mc.mcmodules.view.pinhc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.badoualy.stepperindicator.StepperIndicator
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.FrgDomicilioBinding
import com.mc.mcmodules.model.classes.DataInfoDomicilio
import com.mc.mcmodules.utils.Utils
import com.mc.mcmodules.viewmodel.pinhc.DomicilioViewmodel
import com.tapadoo.alerter.Alerter


class FrgDomicilio(
    private val viewPager: ViewPager2,
    private val stepsView: StepperIndicator,
    private val dataInfoDomicilio: DataInfoDomicilio
) : Fragment() {


    private lateinit var selfViewModel: DomicilioViewmodel
    private lateinit var binding: FrgDomicilioBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initContentView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
        setDataSolicitante()
    }


    private fun initContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        selfViewModel = ViewModelProvider(this).get(DomicilioViewmodel::class.java)
        selfViewModel.setInstanceCompanionViewModel(selfViewModel)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.frg_domicilio, container, false)
        binding.lifecycleOwner = this
        binding.mainView = this
        binding.viewModel = selfViewModel

        return binding.root
    }


    private fun initListeners() {
        with(binding) {
            next.setOnClickListener {


                if (validateFields()) {
                    viewPager.setCurrentItem(2, true)
                    it.postDelayed({
                        stepsView.currentStep = 2
                    }, 300)

                } else {
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_warning)?.let { it1 ->
                        Alerter.create(requireActivity())
                            .setTitle("Campos sin llenar")
                            .setText("Rellena todos los campos por favor")
                            .setIcon(it1)
                            .setBackgroundColorRes(R.color.error)
                            .setDuration(2500)
                            .show()
                    }
                }
            }

            prev.setOnClickListener {

                viewPager.setCurrentItem(0, true)
                it.postDelayed({
                    stepsView.currentStep = 0
                }, 300)


            }
        }

    }

    private fun validateFields(): Boolean {

        with(binding) {


            return Utils.checkField(inCalle) &&
                    Utils.checkField(inNoInt) &&
                    Utils.checkField(inNoExt) &&
                    Utils.checkField(inCP) &&
                    Utils.checkField(inCelular) &&
                    Utils.checkField(inColonia) &&
                    Utils.checkField(inMunicipio) &&
                    Utils.checkField(inCiudad) &&
                    Utils.checkField(inEstado)

        }

    }


    private fun initObservers() {

        selfViewModel.liveDatosDomicilio.observe(requireActivity(), { datosDomicilio ->

            datosDomicilio.LOCK_FIELDS.forEachIndexed { index, lock ->


                with(binding) {
                    when (index) {
                        0 -> inCalle.isEnabled = lock
                        1 -> inNoInt.isEnabled = lock
                        2 -> inNoExt.isEnabled = lock
                        3 -> inCP.isEnabled = lock
                        4 -> inCelular.isEnabled = lock
                        5 -> inColonia.isEnabled = lock
                        6 -> inMunicipio.isEnabled = lock
                        7 -> inCiudad.isEnabled = lock
                        8 -> inEstado.isEnabled = lock

                    }
                }


            }

        })
    }


    private fun setDataSolicitante() {
        selfViewModel.setDataDomicilio(dataInfoDomicilio)
    }

}