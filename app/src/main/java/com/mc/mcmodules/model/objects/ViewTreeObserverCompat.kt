package com.mc.mcmodules.model.objects

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewTreeObserver

object ViewTreeObserverCompat {
    /**
     * Remove a previously installed global layout callback.
     * @param observer the view observer
     * @param victim the victim
     */
    @SuppressLint("NewApi")
    fun removeOnGlobalLayoutListener(
        observer: ViewTreeObserver,
        victim: ViewTreeObserver.OnGlobalLayoutListener?
    ) {
        observer.removeOnGlobalLayoutListener(victim)
    }
}