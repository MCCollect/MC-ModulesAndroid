package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataLogcatInput(
    val fileLogsName: String = "log.txt",
    val maxNumberLogs: Int = 80,
) : Parcelable
