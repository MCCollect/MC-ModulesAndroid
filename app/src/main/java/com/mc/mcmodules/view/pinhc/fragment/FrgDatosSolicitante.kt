package com.mc.mcmodules.view.pinhc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.badoualy.stepperindicator.StepperIndicator
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.FrgDatosSolicitanteBinding
import com.mc.mcmodules.model.classes.DataInfoSolicitante
import com.mc.mcmodules.viewmodel.pinhc.DatosSolicitanteViewmodel

class FrgDatosSolicitante(
    private val viewPager: ViewPager2,
    private val stepsView: StepperIndicator,
    private val dataInfoSolicitante: DataInfoSolicitante
) : Fragment() {

    private lateinit var selfViewModel: DatosSolicitanteViewmodel
    private lateinit var binding: FrgDatosSolicitanteBinding


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
        setDataSolicitante()
    }


    private fun initContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        selfViewModel = ViewModelProvider(this).get(DatosSolicitanteViewmodel::class.java)
        selfViewModel.setInstanceCompanionViewModel(selfViewModel)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.frg_datos_solicitante, container, false)
        binding.lifecycleOwner = this
        binding.mainView = this
        binding.viewModel = selfViewModel

        return binding.root
    }


    private fun initListeners() {
        with(binding) {
            next.setOnClickListener {
                viewPager.setCurrentItem(1, true)
                it.postDelayed({
                    stepsView.currentStep = 1
                }, 300)



            }
        }

    }

    private fun setDataSolicitante(){
        selfViewModel.setDataSolicitante(dataInfoSolicitante)
    }

}