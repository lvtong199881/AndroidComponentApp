package com.mohanlv.router

import androidx.fragment.app.Fragment

/**
 * Auto-generated route table by compile-time scanning
 * DO NOT EDIT MANUALLY
 */
object RouteTable {

    fun registerAll(manager: RouterManager) {
        manager.registerInternal("/home/main") { Class.forName("com.mohanlv.home.ui.HomeFragment").newInstance() as Fragment }
        manager.registerInternal("/common/webview") { Class.forName("com.mohanlv.home.ui.web.WebFragment").newInstance() as Fragment }
        manager.registerInternal("/home/container") { Class.forName("com.mohanlv.home.ui.container.HomeContainerFragment").newInstance() as Fragment }
        manager.registerInternal("/user/collect") { Class.forName("com.mohanlv.user.ui.CollectFragment").newInstance() as Fragment }
        manager.registerInternal("/home/user") { Class.forName("com.mohanlv.user.ui.UserFragment").newInstance() as Fragment }
        manager.registerInternal("/login/main") { Class.forName("com.mohanlv.login.ui.LoginFragment").newInstance() as Fragment }
    }
}
