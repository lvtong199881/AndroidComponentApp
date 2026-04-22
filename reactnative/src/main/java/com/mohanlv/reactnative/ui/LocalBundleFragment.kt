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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mohanlv.reactnative.R
import java.io.File

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

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val toolbarContainer = view.findViewById<View>(R.id.toolbar_container)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadBundles()

        // 处理 WindowInsets，防止被状态栏遮挡
        ViewCompat.setOnApplyWindowInsetsListener(toolbarContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }
    }

    private fun loadBundles() {
        val repos = getLocalRepos()
        if (repos.isEmpty()) {
            recyclerView.adapter = EmptyAdapter()
        } else {
            recyclerView.adapter = RepoAdapter(repos, ::deleteRepo, ::showDeleteVersionDialog)
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

    companion object {
        const val TAG = "LocalBundleFragment"
    }
}
