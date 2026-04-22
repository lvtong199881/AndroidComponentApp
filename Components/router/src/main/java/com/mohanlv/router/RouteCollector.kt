package com.mohanlv.router

/**
 * 路由收集器接口
 * 由 RouteProcessor KAPT 生成实现类
 * 
 * 实现类命名规则：{ModuleName}RouteCollector
 * 例如：HomeRouteCollector、UserRouteCollector
 */
interface RouteCollector {
    /**
     * 注册该模块的所有路由
     * @param manager 路由管理器
     */
    fun register(manager: RouterManager)
}