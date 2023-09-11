package com.mc.mcmodules.view.permissions.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.FragmentResumePermissionsBinding
import com.mc.mcmodules.view.permissions.model.PermissionsResult
import com.mc.mcmodules.view.permissions.view.activities.DinamicPermissionsActivity
import com.mc.mcmodules.view.permissions.view.activities.gone
import com.mc.mcmodules.view.permissions.view.activities.visible
import com.mc.mcmodules.view.permissions.view.adapters.AdapterPermissionsDescription
import com.mc.mcmodules.view.permissions.view.adapters.AdapterPermissionsStatus
import com.mc.mcmodules.view.permissions.viewmodel.ViewModelDinamicPermissions

class ResumePermissionsFragment : Fragment() {
    private lateinit var binding: FragmentResumePermissionsBinding
    private val viewModel: ViewModelDinamicPermissions by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return initBinding(inflater, container)
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentResumePermissionsBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniListeners()
    }

    private fun iniListeners() {
        binding.buttonFinish.setOnClickListener {
            try {
                sendPermissionsStatus()
            }   catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        loadPermissions()
        super.onStart()
    }

    private fun loadPermissions() {
        validatePermissions()
        checkVisibilityButtonFinish()
        loadRecycler()
    }

    private fun validatePermissions() {
        viewModel.listPermissions.forEachIndexed { index, permission ->
            viewModel.listPermissions[index].status = viewModel.checkPermissionIsGranted(
                permission.permission
            )
        }
    }

    private fun checkVisibilityButtonFinish() {
        if (viewModel.checkAllPermissionsStatus()) binding.buttonFinish.visible()
        else binding.buttonFinish.gone()
    }

    private fun loadRecycler() {
        val adapter = AdapterPermissionsStatus(::goToFragmentPage, viewModel.listPermissions)
        binding.recyclerPermissionsData.adapter = adapter
    }

    private fun goToFragmentPage(pageIndex: Int) = viewModel.goToPagePermission(pageIndex)

    private fun sendPermissionsStatus() {
        val resultIntent = Intent()
        resultIntent.putExtra(
            DinamicPermissionsActivity.RESULT_PERMISSIONS,
            PermissionsResult(
                viewModel.checkAllPermissionsStatus()
            )
        )
        requireActivity().setResult(AppCompatActivity.RESULT_OK, resultIntent)
        requireActivity().finish()
    }
}