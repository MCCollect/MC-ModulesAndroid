package com.mc.alternativasur.ui.binding

import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter

class BindingAdapter {

    companion object {
        @BindingAdapter("app:addClickListener")
        @JvmStatic
        fun addClickListener(view: View, listener: View.OnClickListener) {
            view.setOnClickListener(listener)

        }


        @BindingAdapter("app:addTextWatcher")
        @JvmStatic
        fun addTextWatcher(view: View, watcher: TextWatcher) {
            (view as EditText).addTextChangedListener(watcher)

        }


    }


}