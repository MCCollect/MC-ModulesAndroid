package com.mc.alternativasur.ui.binding

import android.view.View
import androidx.databinding.BindingAdapter

class BindingAdapter {

    companion object {
        @BindingAdapter("app:addClickListener")
        @JvmStatic
        fun addClickListener(view: View, listener: View.OnClickListener) {
            view.setOnClickListener(listener)

        }


    }


}