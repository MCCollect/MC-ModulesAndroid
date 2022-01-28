package com.mc.mcmodules.view.escanergenerico.adapter

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.mc.mcmodules.databinding.ViewItemCfeBinding
import com.mc.mcmodules.model.classes.data.ItemScanner
import com.mc.mcmodules.view.escanergenerico.adapter.AdapterFormDocs.ViewHolder

class AdapterFormDocs(
    private val items: List<ItemScanner>,
    private val activity: Activity
) : RecyclerView.Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ViewItemCfeBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.setIsRecyclable(false)
        holder.bind(item, activity)
    }

    class ViewHolder(private val binding: ViewItemCfeBinding) : RecyclerView.ViewHolder(binding.root) {
        private var text: String = ""
        var textPaste: String = ""

        fun bind(item: ItemScanner, activity: Activity) {
            binding.etItemRecycler.hint = item.etiqueta
            item.respuest?.let { text -> binding.etItemRecycler.setText(text)}
            binding.inputItemRecycler.setEndIconOnClickListener {
                imagepaste(binding.inputItemRecycler, binding.etItemRecycler, activity)
            }
            binding.etItemRecycler.addTextChangedListener(object : TextWatcher {
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
                    item.respuest = s.toString()
                }
            })
        }

        fun imagepaste(textInputLayout: TextInputLayout, editText: EditText, activity: Activity) {
            textPaste = pasteData(activity)
            if ("DIRECCION" == textInputLayout.hint) {
                text += "$textPaste "
                editText.setText(text)
                if (text.length > 150) text = ""
            } else editText.setText(textPaste)
        }

        private fun pasteData(activity: Activity): String {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipboard.primaryClip
            val itemAt = clipData!!.getItemAt(0)
            return itemAt.text.toString()
        }
    }
}