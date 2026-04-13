package com.mohanlv.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val DOUBLE_CLICK_INTERVAL = 2000L // 双击间隔 2 秒
    }

    private var lastBackTime = 0L

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

        // 始终跳转到首页
        if (savedInstanceState == null) {
            Log.e(TAG, "跳转到 HomeContainerFragment")
            RouterManager.navigate(RoutePath.HOME_CONTAINER)
        }
        
        // 设置返回键处理
        setupBackHandler()
    }
    
    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackTime < DOUBLE_CLICK_INTERVAL) {
                    // 双击确认退出
                    finish()
                } else {
                    lastBackTime = currentTime
                    Toast.makeText(this@MainActivity, "再按一次退出", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
