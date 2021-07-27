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
import com.mc.mcmodules.databinding.FrgDomicilioBinding
import com.mc.mcmodules.model.classes.DataInfoDomicilio
import com.mc.mcmodules.viewmodel.pinhc.DomicilioViewmodel


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
                viewPager.setCurrentItem(2, true)
                it.postDelayed({
                    stepsView.currentStep = 2
                }, 300)
            }

            prev.setOnClickListener {
                viewPager.setCurrentItem(0, true)
                it.postDelayed({
                    stepsView.currentStep = 0
                }, 300)

            }
        }

    }


    private fun setDataSolicitante(){
        selfViewModel.setDataDomicilio(dataInfoDomicilio)
    }

}