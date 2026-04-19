package com.mohanlv.reactnative.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.shell.MainReactPackage
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.reactnative.databinding.FragmentRnBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route

/**
 * React Native 页面容器 Fragment
 */
@Route(path = RoutePath.RN, description = "React Native 页面")
class RNFragment : BaseFragment<FragmentRnBinding>(), DefaultHardwareBackBtnHandler {

    private var reactRootView: ReactRootView? = null
    private var reactInstanceManager: ReactInstanceManager? = null

    companion object {
        const val KEY_COMPONENT_NAME = "componentName"
        const val DEFAULT_COMPONENT_NAME = "MyRNApp"
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRnBinding {
        return FragmentRnBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initReactNative()
    }

    private fun initReactNative() {
        val componentName = arguments?.getString(KEY_COMPONENT_NAME) ?: DEFAULT_COMPONENT_NAME

        activity?.let { activity ->
            // 获取 ReactNativeHost
            val host = if (activity.application is ReactApplication) {
                (activity.application as ReactApplication).reactNativeHost
            } else {
                createDefaultReactNativeHost(activity)
            }

            // 创建 JSBundleLoader
            val jsBundleLoader = JSBundleLoader.createAssetLoader(
                activity,
                "index.android.bundle",
                false
            )

            // 创建 ReactInstanceManager
            reactInstanceManager = ReactInstanceManager.builder()
                .setCurrentActivity(activity)
                .setApplication(activity.application)
                .setJSMainModulePath("index")
                .setJSBundleLoader(jsBundleLoader)
                .addPackage(MainReactPackage())
                .setUseDeveloperSupport(false)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build()

            // 创建 ReactRootView
            reactRootView = ReactRootView(activity)

            // 构建初始属性
            val initProps = Bundle().apply {
                arguments?.keySet()?.forEach { key ->
                    if (key != KEY_COMPONENT_NAME) {
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

            // 启动 React Native 应用
            reactRootView?.startReactApplication(
                reactInstanceManager,
                componentName,
                initProps
            )

            binding.reactContainer.addView(reactRootView)
        }
    }

    private fun createDefaultReactNativeHost(activity: Activity): ReactNativeHost {
        return object : ReactNativeHost(activity.application) {
            override fun getJSMainModuleName(): String = "index"

            override fun getUseDeveloperSupport(): Boolean = false

            override fun getPackages(): MutableList<com.facebook.react.ReactPackage> {
                return mutableListOf(MainReactPackage())
            }
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

    override fun invokeDefaultOnBackPressed() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }
}
