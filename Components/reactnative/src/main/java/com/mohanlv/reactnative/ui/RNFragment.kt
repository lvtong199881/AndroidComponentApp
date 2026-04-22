package com.mohanlv.reactnative.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.shell.MainReactPackage
import com.mohanlv.reactnative.BundleConfig
import com.mohanlv.reactnative.BundleUrl
import com.mohanlv.reactnative.R
import com.mohanlv.reactnative.ReactNativeHelper
import com.mohanlv.reactnative.databinding.FragmentRnBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * React Native 页面容器 Fragment
 */
@Route(path = RoutePath.RN, description = "React Native 页面")
class RNFragment : Fragment(), DefaultHardwareBackBtnHandler {

    private var viewBinding: FragmentRnBinding? = null
    
    private var reactRootView: ReactRootView? = null
    private var reactInstanceManager: ReactInstanceManager? = null
    private var currentConfig: BundleConfig? = null

    companion object {
        const val KEY_COMPONENT_NAME = "componentName"
        const val KEY_REPO = "repo"
        const val KEY_VERSION = "version"
        const val KEY_BUILD_TYPE = "buildType"
        private const val TAG = "RNFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentRnBinding.inflate(inflater, container, false)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initReactNative()
    }

    private fun initReactNative() {
        val componentName = arguments?.getString(KEY_COMPONENT_NAME)
        val repo = arguments?.getString(KEY_REPO)
        val version = arguments?.getString(KEY_VERSION)
        val buildTypeStr = arguments?.getString(KEY_BUILD_TYPE) ?: "RELEASE"
        val buildType = if (buildTypeStr == "DEBUG") BundleUrl.BuildType.DEBUG else BundleUrl.BuildType.RELEASE
        
        if (componentName.isNullOrBlank() || repo.isNullOrBlank()) {
            closeFragment()
            return
        }
        
        currentConfig = BundleConfig(
            repo = repo,
            version = version,
            buildType = buildType
        )
        
        startLoading()
    }
    
    private fun startLoading() {
        val config = currentConfig ?: return
        val rootView = viewBinding?.reactContainer ?: return
        
        // 显示加载视图
        val loadingView = LayoutInflater.from(context)
            .inflate(R.layout.fragment_rn_loading, rootView, false)
        rootView.addView(loadingView)
        
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val bundleLoader = withContext(Dispatchers.IO) {
                    ReactNativeHelper.resolveBundleLoader(config)
                }
                
                rootView.removeAllViews()
                initReactNativeView(bundleLoader)
                
            } catch (e: Exception) {
                Log.e(TAG, "ReactNative 初始化失败", e)
                rootView.removeAllViews()
                showErrorView(config.showErrorView) { retry() }
            }
        }
    }
    
    private fun retry() {
        startLoading()
    }
    
    private fun showErrorView(show: Boolean, onRetry: () -> Unit) {
        val rootView = viewBinding?.reactContainer ?: return
        
        if (!show) {
            closeFragment()
            return
        }
        
        val errorView = LayoutInflater.from(context)
            .inflate(R.layout.view_rn_error, rootView, false)
        
        errorView.findViewById<View>(R.id.btn_retry)?.setOnClickListener {
            rootView.removeAllViews()
            onRetry()
        }
        
        rootView.addView(errorView)
    }

    private fun initReactNativeView(bundleLoader: com.facebook.react.bridge.JSBundleLoader) {
        val currentActivity = activity ?: return
        val componentName = arguments?.getString(KEY_COMPONENT_NAME) ?: return
        
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
        
        // 创建 ReactInstanceManager
        reactInstanceManager = ReactInstanceManager.builder()
            .setCurrentActivity(activity)
            .setApplication(currentActivity.application)
            .setJSMainModulePath("index")
            .setJSBundleLoader(bundleLoader)
            .addPackage(MainReactPackage())
            .setUseDeveloperSupport(false)
            .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
            .build()
        
        // 启动 React Native 应用
        reactRootView?.startReactApplication(
            reactInstanceManager,
            componentName,
            initProps
        )
        
        viewBinding?.reactContainer?.addView(reactRootView)
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