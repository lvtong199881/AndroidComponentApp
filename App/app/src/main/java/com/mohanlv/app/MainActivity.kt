package com.mohanlv.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mohanlv.base.utils.SPUtils
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 沉浸式状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        
        Log.e(TAG, "========== MainActivity onCreate ==========")
        setContentView(R.layout.activity_main)
        
        RouterManager.currentActivity = this

        // 根据登录状态跳转到不同页面
        if (savedInstanceState == null) {
            val path = if (SPUtils.isLogin) {
                Log.e(TAG, "已登录，跳转到 HomeContainerFragment")
                RoutePath.HOME_CONTAINER
            } else {
                Log.e(TAG, "未登录，跳转到 LoginFragment")
                RoutePath.LOGIN
            }
            RouterManager.navigate(path)
        }
    }
}
