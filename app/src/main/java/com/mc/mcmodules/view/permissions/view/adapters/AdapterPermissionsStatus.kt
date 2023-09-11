package com.mc.mcmodules.view.permissions.view.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mc.mcmodules.R
import com.mc.mcmodules.databinding.ItemResumePermissionBinding
import com.mc.mcmodules.view.permissions.model.Permission

class AdapterPermissionsStatus (
    private val listener: (Int) -> Unit, private val listPermissionData: List<Permission>
): RecyclerView.Adapter<AdapterPermissionsStatus.ViewHolderPermissionStatus>() {
    inner class ViewHolderPermissionStatus(private val binding: ItemResumePermissionBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Permission, index: Int) {
            with(binding) {
                permission.iconPermission.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context, item.icon)
                )
                permission.namePermission.text = item.title
                namePermissionResume.text = item.simplePermission
                if (item.status) {
                    statusPermission.text = "Aceptado"
                    iconStatus.setImageDrawable(
                        ContextCompat.getDrawable(binding.root.context, R.drawable.ic_done)
                    )
                    iconStatus.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(binding.root.context, R.color.success)
                    )
                } else {
                    statusPermission.text = "Rechazado"
                    statusPermission.text = "Rechazado"
                    iconStatus.setImageDrawable(
                        ContextCompat.getDrawable(binding.root.context, R.drawable.ic_close)
                    )
                    iconStatus.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(binding.root.context, R.color.error)
                    )
                }
                root.setOnLongClickListener {
                    listener.invoke(index+1)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderPermissionStatus {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemResumePermissionBinding.inflate(layoutInflater, parent, false)
        return ViewHolderPermissionStatus(itemBinding)
    }

    override fun getItemCount(): Int = listPermissionData.size

    override fun onBindViewHolder(holder: ViewHolderPermissionStatus, position: Int) {
        val item = listPermissionData[position]
        holder.bind(item, position)
    }
}