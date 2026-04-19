package com.mohanlv.reactnative.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.shell.MainReactPackage
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.reactnative.databinding.FragmentRnBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route

/**
 * React Native 页面容器 Fragment
 * 
 * 支持加载任意 React Native 组件，使用方式：
 * 
 * 1. JS 侧注册组件：
 *    AppRegistry.registerComponent('MyComponent', () => MyComponent)
 * 
 * 2. Android 侧跳转：
 *    val args = Bundle().apply {
 *        putString("componentName", "MyComponent")  // 必填，JS 侧注册的组件名
 *        putString("appKey", "MyApp")  // 可选，默认 "MyApp"
 *    }
 *    RouterManager.navigate(RoutePath.RN, args)
 * 
 * 3. JS Bundle 放置位置：
 *    - Debug: 从 Metro 开发服务器加载
 *    - Release: 放入 src/main/assets/index.android.bundle
 */
@Route(path = RoutePath.RN, description = "React Native 页面")
class RNFragment : BaseFragment<FragmentRnBinding>(), DefaultHardwareBackBtnHandler {

    private var reactRootView: ReactRootView? = null
    private var reactInstanceManager: ReactInstanceManager? = null

    companion object {
        const val KEY_COMPONENT_NAME = "componentName"
        const val KEY_APP_KEY = "appKey"
        const val DEFAULT_APP_KEY = "MyApp"
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRnBinding {
        return FragmentRnBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar()
        initReactNative()
    }

    private fun setupToolbar() {
        binding.toolbar?.let { toolbar ->
            val componentName = arguments?.getString(KEY_COMPONENT_NAME) ?: "RN"
            toolbar.title = "RN: $componentName"
            toolbar.setNavigationOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    private fun initReactNative() {
        val componentName = arguments?.getString(KEY_COMPONENT_NAME) ?: run {
            logError("Component name is required")
            return
        }
        val appKey = arguments?.getString(KEY_APP_KEY) ?: DEFAULT_APP_KEY

        activity?.let { activity ->
            // 创建 ReactRootView
            reactRootView = ReactRootView(activity)

            // 构建初始属性
            val initProps = Bundle().apply {
                arguments?.keySet()?.forEach { key ->
                    if (key != KEY_COMPONENT_NAME && key != KEY_APP_KEY) {
                        get(key)?.let { value ->
                            when (value) {
                                is String -> putString(key, value)
                                is Int -> putInt(key, value)
                                is Boolean -> putBoolean(key, value)
                                is Double -> putDouble(key, value)
                            }
                        }
                    }
                }
            }

            // 创建 ReactInstanceManager
            reactInstanceManager = ReactInstanceManager.builder()
                .setApplication(activity.application)
                .setBundleAssetName("index.android.bundle")  // assets 目录下的 bundle 文件
                .setJSMainModulePath("index")  // JS 入口文件名（不含 .js）
                .addPackage(MainReactPackage())
                .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
                .build()

            // 启动 React Native 应用
            reactRootView?.startReactApplication(
                reactInstanceManager,
                componentName,  // JS 侧注册的组件名
                initProps
            )

            binding.reactContainer.addView(reactRootView)
        }
    }

    private fun logError(message: String) {
        activity?.runOnUiThread {
            android.util.Log.e("RNFragment", message)
        }
    }

    override fun onResume() {
        super.onResume()
        reactInstanceManager?.onHostResume(activity, this)
    }

    override fun onPause() {
        super.onPause()
        reactInstanceManager?.onHostPause(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reactInstanceManager?.onHostDestroy(activity)
        reactRootView?.unmountReactApplication()
        reactRootView = null
        reactInstanceManager = null
    }

    /** 处理 RN 页面的返回键 */
    override fun invokeDefaultOnBackPressed() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }
}
