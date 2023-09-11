package com.mc.mcmodules.view.permissions.view.adapters

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mc.mcmodules.databinding.ItemPermissionBinding
import com.mc.mcmodules.view.permissions.model.Permission

class AdapterPermissionsDescription(private val listPermissionData: List<Permission>): RecyclerView.Adapter<AdapterPermissionsDescription.ViewHolderPermissionDescription>() {
    class ViewHolderPermissionDescription(private val binding: ItemPermissionBinding): ViewHolder(binding.root) {
        fun bind(item: Permission) {
            with(binding) {
                iconPermission.setImageDrawable(ContextCompat.getDrawable(binding.root.context, item.icon))
                namePermission.text = item.title
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderPermissionDescription {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemPermissionBinding.inflate(layoutInflater, parent, false)
        return ViewHolderPermissionDescription(itemBinding)
    }

    override fun getItemCount(): Int = listPermissionData.size

    override fun onBindViewHolder(holder: ViewHolderPermissionDescription, position: Int) {
        val item = listPermissionData[position]
        holder.bind(item)
    }
}