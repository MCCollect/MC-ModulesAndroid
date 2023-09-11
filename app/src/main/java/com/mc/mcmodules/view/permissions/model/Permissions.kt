package com.mc.mcmodules.view.permissions.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PermissionsView(
    val permissions: List<Permission>,
    val title: String? = null,
    val descriptionModule: String? = null,
) : Parcelable

@Parcelize
data class Permission(
    val permission: String,
    val simplePermission: String,
    val title: String,
    val description: String,
    val animation: Int,
    val icon: Int,
    val sdkMin: Int,
    val sdkMax: Int,
    var status: Boolean = false,
) : Parcelable

@Parcelize
data class PermissionsResult(
    val isPermissionsAccept: Boolean
):  Parcelable