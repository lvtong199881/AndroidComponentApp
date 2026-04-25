package com.mohanlv.router

/**
 * 路由路径定义
 * 格式：oneandroid://path
 * 外部可通过 oneandroid:// 协议唤起 App 并跳转对应页面
 */
object RoutePath {
    // 路由 Scheme
    const val SCHEME = "oneandroid"
    
    // 登录
    const val LOGIN = "oneandroid://login/main"
    const val LOGIN_REGISTER = "oneandroid://login/register"
    
    // 首页容器
    const val HOME_CONTAINER = "oneandroid://home/container"
    
    // 首页
    const val HOME = "oneandroid://home/main"
    const val HOME_DETAIL = "oneandroid://home/detail"
    
    // 网页
    const val WEB = "oneandroid://home/web"
    
    // 个人中心
    const val USER = "oneandroid://home/user"
    const val PROFILE = "oneandroid://profile/main"
    const val COLLECT_LIST = "oneandroid://user/collect"
    
    // 通用
    const val WEB_VIEW = "oneandroid://common/webview"
    
    // React Native
    const val RN = "oneandroid://common/rn"

    // 短视频
    const val SHORT_VIDEO = "oneandroid://shortvideo/main"
    
    /**
     * 从完整 URL 中提取路径
     * 例如：oneandroid://home/main -> /home/main
     */
    fun extractPath(fullUrl: String): String {
        return fullUrl.substringAfter("://")
    }
    
    /**
     * 判断是否为内部路径（不带 scheme）
     */
    fun isInternalPath(path: String): Boolean {
        return !path.startsWith("oneandroid://")
    }
}
