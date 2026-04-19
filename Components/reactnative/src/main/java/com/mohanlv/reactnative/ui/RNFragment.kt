package com.mohanlv.reactnative.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.mohanlv.reactnative.ReactNativeHelper
import com.mohanlv.reactnative.databinding.FragmentRnBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route

/**
 * React Native 页面容器 Fragment
 */
@Route(path = RoutePath.RN, description = "React Native 页面")
class RNFragment : Fragment(), DefaultHardwareBackBtnHandler {

    private var _binding: FragmentRnBinding? = null
    private val binding get() = _binding!!
    
    private var reactRootView: ReactRootView? = null
    private var reactInstanceManager: ReactInstanceManager? = null

    companion object {
        const val KEY_COMPONENT_NAME = "componentName"
        const val DEFAULT_COMPONENT_NAME = "MyRNApp"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initReactNative()
    }

    private fun initReactNative() {
        val componentName = arguments?.getString(KEY_COMPONENT_NAME) ?: DEFAULT_COMPONENT_NAME

        activity?.let { activity ->
            // 获取 ReactInstanceManager
            val application = activity.application
            val reactNativeHost = if (application is ReactApplication) {
                application.reactNativeHost
            } else {
                throw IllegalStateException("Application must implement ReactApplication")
            }
            reactInstanceManager = ReactNativeHelper.get(application, reactNativeHost)

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
        _binding = null
    }

    override fun invokeDefaultOnBackPressed() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }
}
