package com.mohanlv.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager
import com.mohanlv.reactnative.ui.RNManagerFragment
import com.mohanlv.app.R
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val DOUBLE_CLICK_INTERVAL = 2000L // 双击间隔 2 秒
        private const val DRAG_THRESHOLD = 10 // 移动超过 10px 认为是拖动
    }

    private var lastBackTime = 0L
    private var isDragging = false
    private var dX = 0f
    private var dY = 0f
    private var lastX = 0f
    private var lastY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 启用 edge-to-edge 模式，让内容延伸到系统窗口
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        Log.e(TAG, "========== MainActivity onCreate ==========")
        setContentView(R.layout.activity_main)
        
        RouterManager.currentActivity = this

        // 始终跳转到首页（不添加到返回栈，初始页面不需要）
        if (savedInstanceState == null) {
            Log.e(TAG, "跳转到 HomeContainerFragment")
            RouterManager.navigate(RoutePath.HOME_CONTAINER, addToBackStack = false)
        }
        
        // 设置返回键处理
        setupBackHandler()
        
        // 设置悬浮按钮
        setupFab()
    }
    
    @SuppressLint("ClickableViewAccessibility")
    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fab_bundle_manager)
        fab?.let { fabView ->
            // 设置可拖动
            fabView.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isDragging = false
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                        lastX = event.rawX
                        lastY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = abs(event.rawX - lastX)
                        val dy = abs(event.rawY - lastY)
                        if (dx > DRAG_THRESHOLD || dy > DRAG_THRESHOLD) {
                            isDragging = true
                        }
                        
                        if (isDragging) {
                            val newX = event.rawX + dX
                            val newY = event.rawY + dY
                            
                            // 边界限制
                            val parent = view.parent as? ViewGroup
                            parent?.let {
                                val maxX = it.width - view.width
                                val maxY = it.height - view.height
                                view.x = newX.coerceIn(0f, maxX.toFloat())
                                view.y = newY.coerceIn(0f, maxY.toFloat())
                            }
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isDragging) {
                            // 点击事件，隐藏首页容器，显示 RN 管理页面
                            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
                            currentFragment?.let {
                                supportFragmentManager.beginTransaction()
                                    .hide(it)
                                    .add(R.id.container, RNManagerFragment(), "rn_manager")
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }
    
    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 获取当前容器中的 Fragment
                val containerFragment = supportFragmentManager.findFragmentById(R.id.container)
                val currentTag = containerFragment?.tag
                
                if (currentTag != RoutePath.HOME_CONTAINER) {
                    // 非首页 Fragment，尝试 pop
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                    }
                    return
                }
                // 首页 Fragment，双击退出逻辑
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackTime < DOUBLE_CLICK_INTERVAL) {
                    finish()
                } else {
                    lastBackTime = currentTime
                    Toast.makeText(this@MainActivity, "再按一次退出", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}