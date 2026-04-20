package com.mohanlv.reactnative

/**
 * Bundle 下载配置
 * @param repo GitHub 仓库名
 * @param version 指定版本号，null 表示自动检测最新版本
 */
data class BundleConfig(
    val repo: String = "AndroidComponentApp",
    val version: String? = null,
    val buildType: BundleUrl.BuildType = BundleUrl.BuildType.RELEASE
)

/**
 * 本地 Bundle 信息
 */
data class LocalBundleInfo(
    val version: String,
    val filePath: String
)
