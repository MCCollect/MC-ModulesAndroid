package com.mc.mcmodules.utils

import android.os.Build
import android.view.View

object ViewCompat {
    fun isLaidOut(view: View): Boolean {
        return view.isLaidOut
    }
}