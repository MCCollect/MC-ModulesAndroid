package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class PINRequest(
    var PIN: Int,
    var RESULT: String
) : Parcelable