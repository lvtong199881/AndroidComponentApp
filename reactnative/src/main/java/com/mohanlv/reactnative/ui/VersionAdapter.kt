package com.mohanlv.reactnative.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mohanlv.reactnative.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Bundle 版本列表适配器
 */
class VersionAdapter(
    private val repoName: String,
    private val versions: List<BundleVersion>,
    private val onDelete: (String, String) -> Unit
) : RecyclerView.Adapter<VersionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvVersion: TextView = view.findViewById(R.id.tv_version)
        val tvSize: TextView = view.findViewById(R.id.tv_size)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        val btnDelete: MaterialButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bundle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bundle = versions[position]
        holder.tvVersion.text = bundle.version
        holder.tvSize.text = formatSize(bundle.size)
        holder.tvTime.text = formatTime(bundle.lastModified)
        holder.btnDelete.setOnClickListener {
            onDelete(repoName, bundle.version)
        }
    }

    override fun getItemCount(): Int = versions.size

    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
        }
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}