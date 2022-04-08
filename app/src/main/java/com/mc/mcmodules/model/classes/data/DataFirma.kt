package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DataFirmaRequest (
    var path: String = "",
    var user: String = ""
) : Parcelable

@Parcelize
class DataFirmaResponse (
    var isOk: Boolean = false,
    var message: String = ""
) : Parcelable