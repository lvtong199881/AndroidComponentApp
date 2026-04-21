package com.mohanlv.reactnative.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mohanlv.reactnative.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Bundle 管理弹窗
 * 显示本地存储的所有 bundle 版本
 */
class BundleManagerBottomSheet : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnRefresh: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_bundle_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rv_bundles)
        btnRefresh = view.findViewById(R.id.btn_refresh)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        btnRefresh.setOnClickListener { loadBundles() }

        loadBundles()
    }

    private fun loadBundles() {
        val bundles = getLocalBundles()
        if (bundles.isEmpty()) {
            recyclerView.adapter = EmptyAdapter()
        } else {
            recyclerView.adapter = BundleAdapter(bundles, ::deleteBundle)
        }
    }

    private fun getLocalBundles(): List<LocalBundle> {
        val bundleDir = getBundleDirectory()
        val files = bundleDir.listFiles() ?: return emptyList()

        return files.filter { it.isDirectory && it.name.startsWith("v") }
            .map { dir ->
                val bundleFile = File(dir, "index.android.bundle")
                LocalBundle(
                    version = dir.name,
                    path = bundleFile.absolutePath,
                    size = if (bundleFile.exists()) bundleFile.length() else 0,
                    lastModified = if (bundleFile.exists()) bundleFile.lastModified() else dir.lastModified()
                )
            }
            .sortedByDescending { it.lastModified }
    }

    private fun deleteBundle(bundle: LocalBundle) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bundle_delete_confirm)
            .setPositiveButton(R.string.bundle_delete) { _, _ ->
                val deleted = deleteVersion(bundle.version)
                if (deleted) {
                    Toast.makeText(requireContext(), R.string.bundle_deleted, Toast.LENGTH_SHORT).show()
                    loadBundles()
                } else {
                    Toast.makeText(requireContext(), R.string.bundle_delete_failed, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun deleteVersion(version: String): Boolean {
        val bundleDir = getBundleDirectory()
        val versionDir = File(bundleDir, version)
        return versionDir.exists() && versionDir.deleteRecursively()
    }

    private fun getBundleDirectory(): File {
        val context = requireContext().applicationContext
        return File(context.getDir("react-native", android.content.Context.MODE_PRIVATE), "bundles")
    }

    override fun getTheme(): Int = com.google.android.material.R.style.Theme_MaterialComponents_Light_BottomSheetDialog

    // ============ 数据类 ============

    data class LocalBundle(
        val version: String,
        val path: String,
        val size: Long,
        val lastModified: Long
    )

    // ============ Adapter ============

    class BundleAdapter(
        private val bundles: List<LocalBundle>,
        private val onDelete: (LocalBundle) -> Unit
    ) : RecyclerView.Adapter<BundleAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvVersion: android.widget.TextView = view.findViewById(R.id.tv_version)
            val tvSize: android.widget.TextView = view.findViewById(R.id.tv_size)
            val tvTime: android.widget.TextView = view.findViewById(R.id.tv_time)
            val btnDelete: MaterialButton = view.findViewById(R.id.btn_delete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bundle, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val bundle = bundles[position]
            holder.tvVersion.text = bundle.version
            holder.tvSize.text = formatSize(bundle.size)
            holder.tvTime.text = formatTime(bundle.lastModified)
            holder.btnDelete.setOnClickListener { onDelete(bundle) }
        }

        override fun getItemCount() = bundles.size

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

    class EmptyAdapter : RecyclerView.Adapter<EmptyAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            init { view.findViewById<android.widget.TextView>(R.id.tv_empty).text = "No bundles downloaded yet" }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bundle_empty, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
        override fun getItemCount() = 1
    }
}