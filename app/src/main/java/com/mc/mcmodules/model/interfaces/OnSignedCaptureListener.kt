package com.mc.mcmodules.model.interfaces

import android.graphics.Bitmap

interface OnSignedCaptureListener {
    fun onSignatureCaptured(bitmap: Bitmap, fileUri: String)
}