package com.mohanlv.app

import android.app.Application
import com.mohanlv.base.utils.AppUtils
import com.mohanlv.base.utils.SPUtils
import com.mohanlv.network.NetworkManager
import com.mohanlv.router.RouterManager

class AppApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化工具类
        AppUtils.init(this)
        SPUtils.init(this)
        
        // 初始化网络（使用 WanAndroid API）
        NetworkManager.init(this)
        
        // 初始化路由（自动扫描注册带 @Route 注解的 Fragment）
        RouterManager.init(this, R.id.container)
    }
}
