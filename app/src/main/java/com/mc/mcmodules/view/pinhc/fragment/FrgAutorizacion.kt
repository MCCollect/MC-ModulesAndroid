package com.mc.mcmodules.view.pinhc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.badoualy.stepperindicator.StepperIndicator
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.FrgAutorizacionBinding
import com.mc.mcmodules.model.classes.DataInfoAutorizacion
import com.mc.mcmodules.viewmodel.pinhc.AutorizacionViewmodel

class FrgAutorizacion(
    private val viewPager: ViewPager2,
    private val stepsView: StepperIndicator,
    private val dataInfoAutorizacion: DataInfoAutorizacion
) : Fragment() {

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

            prev.setOnClickListener {
                viewPager.setCurrentItem(1, true)
                it.postDelayed({
                    stepsView.currentStep = 1
                }, 300)

            }
        }

    }

    private fun setDataAutorizacion() {
        selfViewModel.setDataAutorizacion(dataInfoAutorizacion)
    }

    private fun initObservers() {

        selfViewModel.liveDatosAutorizacion.observe(requireActivity(), { dataInfoAutorizacion ->


            with(binding) {

                txtAcepterminos.text = HtmlCompat.fromHtml(
                    dataInfoAutorizacion.TEXT1_CHECKBOX,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
                text1.text = HtmlCompat.fromHtml(
                    dataInfoAutorizacion.TEXT2_TITLE,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )


                text2.text = HtmlCompat.fromHtml(
                    dataInfoAutorizacion.TEXT3_PARRAFO1,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )

                text3.text =HtmlCompat.fromHtml(
                    dataInfoAutorizacion.TEXT4_PARRAFO2,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )



            }


        })

    }


}