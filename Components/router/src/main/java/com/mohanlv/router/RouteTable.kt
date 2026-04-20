package com.mohanlv.router

import androidx.fragment.app.Fragment

object RouteTable {

    fun registerAll(manager: RouterManager) {
        manager.registerInternal("home/main") { Class.forName("com.mohanlv.home.ui.HomeFragment").getDeclaredConstructor().newInstance() as Fragment }
        manager.registerInternal("common/webview") { Class.forName("com.mohanlv.home.ui.web.WebFragment").getDeclaredConstructor().newInstance() as Fragment }
        manager.registerInternal("home/container") { Class.forName("com.mohanlv.home.ui.container.HomeContainerFragment").getDeclaredConstructor().newInstance() as Fragment }
        manager.registerInternal("user/collect") { Class.forName("com.mohanlv.user.ui.CollectFragment").getDeclaredConstructor().newInstance() as Fragment }
        manager.registerInternal("home/user") { Class.forName("com.mohanlv.user.ui.UserFragment").getDeclaredConstructor().newInstance() as Fragment }
        manager.registerInternal("login/main") { Class.forName("com.mohanlv.login.ui.LoginFragment").getDeclaredConstructor().newInstance() as Fragment }
        manager.registerInternal("common/rn") { Class.forName("com.mohanlv.reactnative.ui.RNFragment").getDeclaredConstructor().newInstance() as Fragment }
    }
}
