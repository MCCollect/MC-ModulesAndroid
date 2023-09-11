package com.mc.mcmodules.view.permissions.view.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.FragmentPresentationBinding
import com.mc.mcmodules.view.permissions.view.activities.gone
import com.mc.mcmodules.view.permissions.view.activities.isPortraitMode
import com.mc.mcmodules.view.permissions.view.activities.visible
import com.mc.mcmodules.view.permissions.view.adapters.AdapterPermissionsDescription
import com.mc.mcmodules.view.permissions.viewmodel.ViewModelDinamicPermissions

class PresentationPermissionsFragment: Fragment() {
    private lateinit var binding: FragmentPresentationBinding
    private val viewModel: ViewModelDinamicPermissions by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = initBinding(inflater, container)

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentPresentationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListeners()
    }

    private fun initData() {
        with(binding) {
            visibilityOrientatition(requireContext().isPortraitMode())
            title.text = viewModel.titleFragment ?: getText(R.string.default_dinamic_permisssions_title)
            description.text = viewModel.descriptionFragment ?: getText(R.string.default_dinamic_permisssions_description)

            val layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerPermissionsData.layoutManager = layoutManager
            val adapter = AdapterPermissionsDescription(viewModel.listPermissions)
            recyclerPermissionsData.adapter = adapter
        }
    }

    private fun initListeners() {
        binding.buttonStart.setOnClickListener { viewModel.goToNextPage(true) }
    }

    override fun onStart() {
        binding.animationPermission.playAnimation()
        super.onStart()
    }

    override fun onPause() {
        binding.animationPermission.pauseAnimation()
        super.onPause()
    }

    override fun onDestroy() {
        binding.animationPermission.clearAnimation()
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        visibilityOrientatition(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
    }

    private fun visibilityOrientatition(isPortrait: Boolean) {
        if (isPortrait) {
            binding.animationPermission.visible()
            binding.recyclerPermissionsData.visible()
        } else {
            binding.animationPermission.gone()
            binding.recyclerPermissionsData.gone()
        }
    }
}