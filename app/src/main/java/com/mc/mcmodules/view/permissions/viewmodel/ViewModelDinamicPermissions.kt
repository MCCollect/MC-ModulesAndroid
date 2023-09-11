package com.mc.mcmodules.view.permissions.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mc.mcmodules.view.permissions.model.Permission
import java.lang.ref.WeakReference

class ViewModelDinamicPermissions(context: Context): ViewModel() {
    private val weakContext = WeakReference(context)
    var titleFragment: String? = null
    var descriptionFragment: String? = null
    val listPermissions: MutableList<Permission> = mutableListOf()

    private var _goToNextPage: MutableLiveData<Boolean> = MutableLiveData(false)
    val goToNextPage: LiveData<Boolean> get() = _goToNextPage
    fun goToNextPage(status: Boolean) = _goToNextPage.postValue(status)

    private var _requestPermission: MutableLiveData<Permission?> = MutableLiveData()
    val requestPermission: LiveData<Permission?> get() = _requestPermission
    fun requestPermission(permission: Permission) = _requestPermission.postValue(permission)

    private var _changeStatusPermission: MutableLiveData<Boolean> = MutableLiveData(false)
    val changeStatusPermission: LiveData<Boolean> get() = _changeStatusPermission
    fun changeStatusPermission(status: Boolean) = _changeStatusPermission.postValue(status)

    private var _goToPagePermission: MutableLiveData<Int?> = MutableLiveData()
    val goToPagePermission: LiveData<Int?> get() = _goToPagePermission
    fun goToPagePermission(page: Int?) = _goToPagePermission.postValue(page)

    fun checkAllPermissionsStatus(): Boolean {
        var status = true
        listPermissions.forEach { permission ->
            val checkPermission = checkPermissionIsGranted(permission.permission)
            status = status && checkPermission
        }
        return status
    }

    fun checkPermissionIsGranted(permission: String): Boolean {
        weakContext.get()?.let { context ->
            return ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED
        }
        return false
    }
}