package com.mc.mcmodules.view.adapters.pinhc

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.badoualy.stepperindicator.StepperIndicator
import com.mc.mcmodules.model.classes.data.DataInfoAutorizacion
import com.mc.mcmodules.model.classes.data.DataInfoDomicilio
import com.mc.mcmodules.model.classes.data.DataInfoSolicitante
import com.mc.mcmodules.view.pinhc.fragment.FrgAutorizacion
import com.mc.mcmodules.view.pinhc.fragment.FrgDatosSolicitante
import com.mc.mcmodules.view.pinhc.fragment.FrgDomicilio


class PagerFragmentAdapter(
    activity: AppCompatActivity,
    private val pager2: ViewPager2,
    private val stepsView: StepperIndicator,
    private val dataInfoSolicitante: DataInfoSolicitante,
    private val dataInfoDomicilio: DataInfoDomicilio,
    private val dataInfoAutorizacion: DataInfoAutorizacion
) :
    FragmentStateAdapter(
        activity
    ) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FrgDatosSolicitante(pager2, stepsView, dataInfoSolicitante)
            1 -> FrgDomicilio(pager2, stepsView, dataInfoDomicilio)
            else -> FrgAutorizacion(pager2, stepsView, dataInfoAutorizacion)
        }
    }


}