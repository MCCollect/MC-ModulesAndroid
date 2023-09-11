package com.mc.mcmodules.view.permissions.view.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.FragmentDinamicPermissionBinding
import com.mc.mcmodules.view.permissions.model.Permission
import com.mc.mcmodules.view.permissions.view.activities.gone
import com.mc.mcmodules.view.permissions.view.activities.isPortraitMode
import com.mc.mcmodules.view.permissions.view.activities.visible
import com.mc.mcmodules.view.permissions.viewmodel.ViewModelDinamicPermissions

class DinamicPermissionFragment : Fragment() {
    private lateinit var binding: FragmentDinamicPermissionBinding
    private val viewModel: ViewModelDinamicPermissions by activityViewModels()
    private var permissionData: Permission? = null
    companion object {
        const val PERMISSION = "permission"
        fun newInstanceWithArguments(param1: Permission): DinamicPermissionFragment {
            val fragment = DinamicPermissionFragment()
            val args = Bundle()
            args.putParcelable(PERMISSION, param1)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initBinding(inflater, container)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentDinamicPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveBundleData()
        initData()
        initListeners()
        initObservers()
    }

    private fun saveBundleData() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelable(PERMISSION, Permission::class.java)
        else arguments?.getParcelable(PERMISSION)

        if (permission != null) {
            permissionData = permission
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        with(binding) {
            val permissionText = permissionData?.simplePermission ?: "Error al cargar permiso"
            val titleText = permissionData?.title ?: "Error al cargar titulo"
            val description = permissionData?.description ?: "Error al cargar descripcion"
            val icon = permissionData?.icon ?: R.drawable.ic_settings
            val animation = permissionData?.animation ?: R.raw.animation_permission_default

            viewPermissionTitle.namePermission.text = titleText
            viewPermissionTitle.iconPermission.setImageDrawable(ContextCompat.getDrawable(
                binding.root.context, icon
            ))
            viewPermissionTitle.root.rotation = -16f
            descriptionPermission.text = description
            permission.text = permissionText
            animationPermissionOne.setAnimation(animation)

            visibilityOrientatition(requireContext().isPortraitMode())
            visibilityPermissionViews()
        }
    }

    private fun visibilityPermissionViews() {
        permissionData?.let { data ->
            if (viewModel.checkPermissionIsGranted(data.permission)) showOkPermissionView()
            else showDescriptionPermissionView()
        }
    }

    private fun initListeners() {
        binding.buttonPermission.setOnClickListener { requestPermission() }
        binding.buttonNext.setOnClickListener { viewModel.goToNextPage(true) }
    }

    private fun requestPermission() {
        permissionData?.let { data ->
            if (viewModel.checkPermissionIsGranted(data.permission)) showOkPermissionView()
            else viewModel.requestPermission(data)
        }
    }

    private fun showOkPermissionView() {
        binding.viewPermissionOk.visible()
        binding.animationPermissionSuccess.setAnimation(R.raw.animation_success)
        binding.animationPermissionSuccess.playAnimation()
    }

    private fun showDescriptionPermissionView() {
        binding.viewPermissionOk.gone()
        binding.animationPermissionOne.setAnimation(R.raw.animation_permission)
        binding.animationPermissionOne.playAnimation()
    }

    private fun initObservers() {
        viewModel.changeStatusPermission.observe(viewLifecycleOwner) { status ->
            if (status) {
                try {
                    visibilityPermissionViews()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onResume() {
        visibilityPermissionViews()
        super.onResume()
    }

    override fun onPause() {
        binding.animationPermissionOne.pauseAnimation()
        binding.animationPermissionSuccess.pauseAnimation()
        super.onPause()
    }

    override fun onDestroy() {
        binding.animationPermissionOne.clearAnimation()
        binding.animationPermissionSuccess.clearAnimation()
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        visibilityOrientatition(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
    }

    private fun visibilityOrientatition(isPortrait: Boolean) {
        if (isPortrait) {
            binding.viewPermissionTitle.root.visible()
            binding.animationPermissionOne.visible()
            binding.animationPermissionSuccess.visible()
        } else {
            binding.viewPermissionTitle.root.gone()
            binding.animationPermissionOne.gone()
            binding.animationPermissionSuccess.gone()
        }
    }
}