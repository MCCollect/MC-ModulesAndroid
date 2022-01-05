package com.mc.mcmodules.view.escanergenerico.adapter

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.mc.mcmodules.R
import com.mc.mcmodules.model.classes.data.ItemScanner
import com.mc.mcmodules.view.escanergenerico.adapter.adapterFormDocs.ViewHolder
import kotlin.collections.ArrayList

class adapterFormDocs(
    private val items: ArrayList<ItemScanner>,
    private val Layout: Int = 0,
    private val activity: Activity
) : RecyclerView.Adapter<ViewHolder>() {
    private var text: String = ""
    private lateinit var textSet: String
    var textPaste: String = ""

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var editText: EditText = itemView.findViewById<View>(R.id.etItemRecycler) as EditText
        var textInputLayout: TextInputLayout =
            itemView.findViewById<View>(R.id.inputItemRecycler) as TextInputLayout

        init {
            textInputLayout.setEndIconOnClickListener {
                imagepaste(textInputLayout, editText)
                oyenteEdit(editText)
            }
        }

        fun bind(position: Int, items: ArrayList<ItemScanner>) {
            textInputLayout.hint = items[position].etiqueta
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    println("BeforeTextChanged")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    println("OnTextChanged")
                }

                override fun afterTextChanged(s: Editable?) {
                    items[position].respuest = s.toString()
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(Layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, items)
    }

    /*MÉTODO PARA SETTEAR EL TEXTO EN EL EDITTEXT*/
    fun imagepaste(textInputLayout: TextInputLayout, editText: EditText) {
        textPaste = pasteData()
        if ("DIRECCION" == textInputLayout.hint) {
            text += "$textPaste "
            editText.setText(text)
            if (text.length > 150) {
                text = ""
            }
        } else {
            editText.setText(textPaste)
        }
//        editText.setText(textPaste);
    }

    /*MÉTODO PARA PEGAR EL TEXTO*/
    private fun pasteData(): String {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        val itemAt = clipData!!.getItemAt(0)
        return itemAt.text.toString()
    }

    /*MÉTODOS DE PRUEBA*/
    fun oyenteVacio(text: String) {
        Toast.makeText(activity.applicationContext, "" + text, Toast.LENGTH_LONG)
            .show()
    }

    fun oyente(textInputLayout: TextInputLayout) {
        Toast.makeText(activity.applicationContext, "" + textInputLayout.hint, Toast.LENGTH_LONG)
            .show()
    }

    fun oyenteEdit(editText: EditText) {
        /*Toast.makeText(activity.applicationContext, ""
                + "\n"+
                editText.text.toString(), Toast.LENGTH_LONG)
            .show()*/
    }
}