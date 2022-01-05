package com.mc.mcmodules.model.classes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DataDocs(
    var camposDocScaneado: ArrayList<String>,
    var camposDoc: ArrayList<String>
) : Parcelable