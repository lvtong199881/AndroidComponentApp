package com.mohanlv.reactnative.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mohanlv.reactnative.R

/**
 * Repository Bundle 列表适配器
 */
class RepoAdapter(
    private val repos: List<RepoBundle>,
    private val onDeleteRepo: (RepoBundle) -> Unit,
    private val onDeleteVersion: (String, String) -> Unit,
) : RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRepoName: TextView = view.findViewById(R.id.tv_repo_name)
        val tvVersionCount: TextView = view.findViewById(R.id.tv_version_count)
        val tvTotalSize: TextView = view.findViewById(R.id.tv_total_size)
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

    override fun getItemCount(): Int = repos.size

    private fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
        }
    }
}