package com.mohanlv.reactnative

/**
 * Bundle 下载配置
 * @param repo GitHub 仓库名
 * @param version 指定版本号，null 表示自动检测最新版本
 * @param maxRetry 下载失败时的最大重试次数，默认 3 次
 * @param showErrorView 下载失败后是否显示错误视图，默认 true
 */
data class BundleConfig(
    val repo: String = "AndroidComponentApp",
    val version: String? = null,
    val buildType: BundleUrl.BuildType = BundleUrl.BuildType.RELEASE,
    val maxRetry: Int = 3,
    val showErrorView: Boolean = true
)

/**
 * 本地 Bundle 信息
 */
data class LocalBundleInfo(
    val version: String,
    val filePath: String
)
