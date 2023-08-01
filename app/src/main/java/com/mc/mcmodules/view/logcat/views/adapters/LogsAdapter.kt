package com.mc.mcmodules.view.logcat.views.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mc.mcmodules.databinding.ItemLogBinding
import com.mc.mcmodules.view.logcat.models.Log

class LogsAdapter(private val listLogs: MutableList<Log>): RecyclerView.Adapter<ViewHolderLog>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderLog {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLogBinding.inflate(inflater, parent, false)
        return ViewHolderLog(binding)
    }

    override fun getItemCount(): Int = listLogs.size

    override fun onBindViewHolder(holder: ViewHolderLog, position: Int) {
        val item = listLogs[position]
        holder.onBind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun populateData(list: List<Log>) {
        listLogs.clear()
        listLogs.addAll(list)
        notifyDataSetChanged()
    }
}

class ViewHolderLog(val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: Log) {
        binding.iconLog.setImageResource(item.image)
        binding.log = item
    }
}