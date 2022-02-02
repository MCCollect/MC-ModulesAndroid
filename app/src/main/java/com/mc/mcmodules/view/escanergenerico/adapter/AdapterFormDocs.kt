package com.mc.mcmodules.view.escanergenerico.adapter

import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.mc.mcmodules.databinding.ViewItemCfeBinding
import com.mc.mcmodules.model.classes.data.ItemScanner
import com.mc.mcmodules.view.escanergenerico.adapter.AdapterFormDocs.ViewHolder

class AdapterFormDocs(private val items: List<ItemScanner>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ViewItemCfeBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(private val binding: ViewItemCfeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemScanner) {

            binding.inputItemRecycler.editText?.apply {

                setText(item.respuest)

                val watcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) { item.respuest = s.toString() }
                }

                setOnFocusChangeListener { _, isFocus ->
                    if (isFocus) { addTextChangedListener(watcher) }
                    else { removeTextChangedListener(watcher) }
                }

                binding.inputItemRecycler.let { inp ->
                    inp.setEndIconOnClickListener { _ ->
                        item.respuest = imagepaste(this)
                    }
                    inp.hint = item.etiqueta
                }
            }
        }

        fun imagepaste(editText: EditText): String {
            var text = editText.text.toString().trim()
            text = "$text ${pasteData(editText.context)}"
            editText.setText(text)
            return text
        }

        private fun pasteData(context: Context): String {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.primaryClip?.let { clipData ->
                clipData.getItemAt(0)?.let { itemAt ->
                    return itemAt.text.toString()
                }
            }
            return ""
        }
    }
}