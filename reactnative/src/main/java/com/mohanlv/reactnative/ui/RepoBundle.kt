package com.mohanlv.reactnative.ui

/**
 * 本地仓库 Bundle 数据
 */
data class RepoBundle(
    val repoName: String,
    val versions: List<BundleVersion>,
    val totalSize: Long
)

/**
 * Bundle 版本信息
 */
data class BundleVersion(
    val version: String,
    val path: String,
    val size: Long,
    val lastModified: Long
)