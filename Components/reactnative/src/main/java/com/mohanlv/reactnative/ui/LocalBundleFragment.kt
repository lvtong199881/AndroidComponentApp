package com.mohanlv.reactnative.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mohanlv.reactnative.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 本地 Bundle 包管理 - 二级页面
 */
class LocalBundleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_local_bundle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        recyclerView = view.findViewById(R.id.rv_bundles)

        val toolbarContainer = view.findViewById<View>(R.id.toolbar_container)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 处理 WindowInsets，防止被状态栏遮挡
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            toolbarContainer.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadBundles()
    }

    private fun loadBundles() {
        val repos = getLocalRepos()
        if (repos.isEmpty()) {
            recyclerView.adapter = EmptyAdapter()
        } else {
            recyclerView.adapter = RepoAdapter(repos, ::deleteRepo, ::showDeleteVersionDialog, ::loadBundles)
        }
    }

    /**
     * 获取本地仓库分组
     * 结构: bundles/{repo}/{version}/index.android.bundle
     */
    private fun getLocalRepos(): List<RepoBundle> {
        val bundleDir = getBundleDirectory()
        val repoDirs = bundleDir.listFiles() ?: return emptyList()

        return repoDirs.filter { it.isDirectory && !it.name.startsWith(".") }
            .map { repoDir ->
                val versions = repoDir.listFiles()
                    ?.filter { it.isDirectory && it.name.startsWith("v") }
                    ?.map { verDir ->
                        val bundleFile = File(verDir, "index.android.bundle")
                        BundleVersion(
                            version = verDir.name,
                            path = bundleFile.absolutePath,
                            size = if (bundleFile.exists()) bundleFile.length() else 0,
                            lastModified = if (bundleFile.exists()) bundleFile.lastModified() else verDir.lastModified()
                        )
                    }
                    ?.sortedByDescending { it.lastModified }
                    ?: emptyList()

                RepoBundle(
                    repoName = repoDir.name,
                    versions = versions,
                    totalSize = versions.sumOf { it.size }
                )
            }
            .filter { it.versions.isNotEmpty() }
            .sortedByDescending { it.versions.maxOfOrNull { v -> v.lastModified } ?: 0 }
    }

    private fun showDeleteVersionDialog(repo: String, version: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bundle_delete_confirm)
            .setMessage(getString(R.string.bundle_delete_version_confirm, version))
            .setPositiveButton(R.string.bundle_delete) { _, _ ->
                val deleted = doDeleteVersion(repo, version)
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

    private fun deleteRepo(repo: RepoBundle) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bundle_delete_confirm)
            .setMessage(getString(R.string.bundle_delete_repo_confirm, repo.repoName))
            .setPositiveButton(R.string.bundle_delete) { _, _ ->
                val deleted = deleteRepoBundle(repo.repoName)
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

    private fun deleteRepoBundle(repoName: String): Boolean {
        val bundleDir = getBundleDirectory()
        val repoDir = File(bundleDir, repoName)
        return repoDir.exists() && repoDir.deleteRecursively()
    }

    private fun doDeleteVersion(repoName: String, version: String): Boolean {
        val bundleDir = getBundleDirectory()
        val versionDir = File(bundleDir, "$repoName/$version")
        return versionDir.exists() && versionDir.deleteRecursively()
    }

    private fun getBundleDirectory(): File {
        val context = requireContext().applicationContext
        return File(context.getDir("react-native", android.content.Context.MODE_PRIVATE), "bundles")
    }

    // ============ 数据类 ============

    data class RepoBundle(
        val repoName: String,
        val versions: List<BundleVersion>,
        val totalSize: Long
    )

    data class BundleVersion(
        val version: String,
        val path: String,
        val size: Long,
        val lastModified: Long
    )

    // ============ Repository Adapter ============

    class RepoAdapter(
        private val repos: List<RepoBundle>,
        private val onDeleteRepo: (RepoBundle) -> Unit,
        private val onDeleteVersion: (String, String) -> Unit,
        private val onRefresh: () -> Unit
    ) : RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvRepoName: android.widget.TextView = view.findViewById(R.id.tv_repo_name)
            val tvVersionCount: android.widget.TextView = view.findViewById(R.id.tv_version_count)
            val tvTotalSize: android.widget.TextView = view.findViewById(R.id.tv_total_size)
            val rvVersions: RecyclerView = view.findViewById(R.id.rv_versions)
            val btnDeleteRepo: MaterialButton = view.findViewById(R.id.btn_delete_repo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_repo_bundle, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val repo = repos[position]
            holder.tvRepoName.text = repo.repoName
            holder.tvVersionCount.text = holder.itemView.context.getString(R.string.bundle_version_count, repo.versions.size)
            holder.tvTotalSize.text = formatSize(repo.totalSize)
            holder.btnDeleteRepo.setOnClickListener { onDeleteRepo(repo) }

            holder.rvVersions.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.rvVersions.adapter = VersionAdapter(repo.repoName, repo.versions, onDeleteVersion)
        }

        override fun getItemCount() = repos.size

        private fun formatSize(bytes: Long): String {
            return when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> "${bytes / 1024} KB"
                else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            }
        }
    }

    // ============ Version Adapter ============

    class VersionAdapter(
        private val repoName: String,
        private val versions: List<BundleVersion>,
        private val onDelete: (String, String) -> Unit
    ) : RecyclerView.Adapter<VersionAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvVersion: android.widget.TextView = view.findViewById(R.id.tv_version)
            val tvSize: android.widget.TextView = view.findViewById(R.id.tv_size)
            val tvTime: android.widget.TextView = view.findViewById(R.id.tv_time)
            val btnDelete: com.google.android.material.button.MaterialButton = view.findViewById(R.id.btn_delete)
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

        override fun getItemCount() = versions.size

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

    // ============ Empty Adapter ============

    class EmptyAdapter : RecyclerView.Adapter<EmptyAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            init {
                val textView = view.findViewById<android.widget.TextView>(R.id.tv_empty)
                textView.text = view.context.getString(R.string.bundle_empty)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bundle_empty, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
        override fun getItemCount() = 1
    }

    companion object {
        const val TAG = "LocalBundleFragment"
    }
}
