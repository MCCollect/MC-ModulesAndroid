package com.mc.mcmodules.view.logcat.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Log (
    val image: Int,
    val date: String,
    val description: String,
    val type: String = "undefined",
) : Parcelable
