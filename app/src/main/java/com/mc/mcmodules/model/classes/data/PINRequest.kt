package com.mc.mcmodules.model.classes.data

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class PINRequest(
    var PIN: Int,
    var RESULT: String,
    var FIRMA: String
) : Parcelable