package com.mohanlv.reactnative.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import com.mohanlv.reactnative.BundleConfig
import com.mohanlv.reactnative.BundleUrl
import com.mohanlv.reactnative.ReactNativeHelper
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route

/**
 * React Native 页面容器 Fragment
 */
@Route(path = RoutePath.RN, description = "React Native 页面")
class RNFragment : Fragment(), DefaultHardwareBackBtnHandler {

    private var viewBinding: com.mohanlv.reactnative.databinding.FragmentRnBinding? = null
    
    private var reactRootView: ReactRootView? = null
    private var reactInstanceManager: ReactInstanceManager? = null

    companion object {
        const val KEY_COMPONENT_NAME = "componentName"
        const val KEY_REPO = "repo"
        const val KEY_VERSION = "version"
        const val KEY_BUILD_TYPE = "buildType"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = com.mohanlv.reactnative.databinding.FragmentRnBinding.inflate(inflater, container, false)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initReactNative()
    }

    private fun initReactNative() {
        // 从 URL 参数获取配置，repo 和 componentName 必须有值
        val componentName = arguments?.getString(KEY_COMPONENT_NAME)
        val repo = arguments?.getString(KEY_REPO)
        val version = arguments?.getString(KEY_VERSION)
        val buildTypeStr = arguments?.getString(KEY_BUILD_TYPE) ?: "RELEASE"
        val buildType = if (buildTypeStr == "DEBUG") BundleUrl.BuildType.DEBUG else BundleUrl.BuildType.RELEASE
        
        // repo 和 componentName 必须提供，否则关闭 fragment
        if (componentName.isNullOrBlank() || repo.isNullOrBlank()) {
            closeFragment()
            return
        }
        
        // 构建 Bundle 配置
        val config = BundleConfig(
            repo = repo,
            version = version,
            buildType = buildType
        )
        
        val currentActivity = activity ?: return
        
        // 创建 ReactInstanceManager（带正确的 bundle loader）
        reactInstanceManager = createReactInstanceManager(currentActivity.application, config)

        // 创建 ReactRootView
        reactRootView = ReactRootView(currentActivity)

        // 构建初始属性
        val initProps = Bundle().apply {
            arguments?.keySet()?.forEach { key ->
                if (key !in listOf(KEY_COMPONENT_NAME, KEY_REPO, KEY_VERSION, KEY_BUILD_TYPE)) {
                    when (val value = arguments?.get(key)) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Boolean -> putBoolean(key, value)
                        is Double -> putDouble(key, value)
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

        viewBinding!!.root.addView(reactRootView)
    }
    
    private fun createReactInstanceManager(app: android.app.Application, config: BundleConfig): ReactInstanceManager {
        // 获取 bundle loader（内部自动处理下载/版本检测）
        val jsBundleLoader = ReactNativeHelper.resolveBundleLoader(config)
        
        return ReactInstanceManager.builder()
            .setCurrentActivity(activity)
            .setApplication(app)
            .setJSMainModulePath("index")
            .setJSBundleLoader(jsBundleLoader)
            .addPackage(MainReactPackage())
            .setUseDeveloperSupport(false)
            .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
            .build()
    }

    private fun closeFragment() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.remove(this)
            ?.commitAllowingStateLoss()
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
        viewBinding = null
    }

    override fun invokeDefaultOnBackPressed() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }
}
