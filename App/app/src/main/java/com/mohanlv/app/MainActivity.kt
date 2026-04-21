package com.mohanlv.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager
import com.mohanlv.reactnative.ui.BundleManagerBottomSheet

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val DOUBLE_CLICK_INTERVAL = 2000L // 双击间隔 2 秒
    }

    private var lastBackTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 启用 edge-to-edge 模式，让内容延伸到系统窗口
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
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
        
        // 设置悬浮按钮
        setupFab()
    }
    
    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fab_bundle_manager)
        fab?.setOnClickListener {
            BundleManagerBottomSheet().show(supportFragmentManager, "BundleManager")
        }
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
