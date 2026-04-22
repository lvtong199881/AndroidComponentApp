package com.mohanlv.reactnative.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import com.mohanlv.reactnative.logE
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.shell.MainReactPackage
import com.mohanlv.reactnative.BundleConfig
import com.mohanlv.reactnative.R
import com.mohanlv.reactnative.ReactNativeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * React Native 容器视图
 * 封装 loading / error / ReactRootView 状态切换
 */
class RNView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), DefaultHardwareBackBtnHandler {

    private var reactRootView: ReactRootView? = null
    private var reactInstanceManager: ReactInstanceManager? = null
    private var currentConfig: BundleConfig? = null
    private var currentProps: Bundle = Bundle()

    private lateinit var loadingView: RNLoadingView
    private lateinit var errorView: RNErrorView
    private lateinit var contentContainer: FrameLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.view_rn, this, true)

        loadingView = findViewById(R.id.loadingView)
        errorView = findViewById(R.id.errorView)
        contentContainer = findViewById(R.id.contentContainer)

        errorView.setOnRetryListener {
            showLoading()
            loadBundle()
        }
    }

    /**
     * 开始加载 React Native
     * @param config Bundle 配置
     * @param extraProps 额外传递给 RN 的属性
     */
    fun startLoading(config: BundleConfig, extraProps: Bundle = Bundle()) {
        currentConfig = config
        currentProps = extraProps
        showLoading()
        loadBundle()
    }

    private fun showLoading() {
        loadingView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
        contentContainer.visibility = View.GONE
    }

    private fun showError() {
        val config = currentConfig ?: return
        if (!config.showErrorView) {
            loadBundle()
            return
        }
        loadingView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        contentContainer.visibility = View.GONE
    }

    private fun showContent() {
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE
        contentContainer.visibility = View.VISIBLE
    }

    private fun loadBundle() {
        val config = currentConfig ?: return

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val bundleLoader = withContext(Dispatchers.IO) {
                    ReactNativeHelper.resolveBundleLoader(config)
                }

                initReactNativeView(bundleLoader, currentProps)
                showContent()

            } catch (e: Exception) {
                logE("RNView::ReactNative 初始化失败", e)
                showError()
            }
        }
    }

    private fun initReactNativeView(bundleLoader: com.facebook.react.bridge.JSBundleLoader, extraProps: Bundle) {
        val activity = context as? androidx.appcompat.app.AppCompatActivity ?: return
        val componentName = currentConfig?.repo ?: return

        reactRootView = ReactRootView(activity)

        val initProps = Bundle().apply { putAll(extraProps) }

        reactInstanceManager = ReactInstanceManager.builder()
            .setCurrentActivity(activity)
            .setApplication(activity.application)
            .setJSMainModulePath("index")
            .setJSBundleLoader(bundleLoader)
            .addPackage(MainReactPackage())
            .setUseDeveloperSupport(false)
            .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
            .build()

        reactRootView?.startReactApplication(reactInstanceManager, componentName, initProps)

        contentContainer.removeAllViews()
        contentContainer.addView(reactRootView)
    }

    fun onResume() {
        val activity = context as? androidx.appcompat.app.AppCompatActivity ?: return
        reactInstanceManager?.onHostResume(activity, this)
    }

    fun onPause() {
        val activity = context as? androidx.appcompat.app.AppCompatActivity ?: return
        reactInstanceManager?.onHostPause(activity)
    }

    fun onDestroy() {
        val activity = context as? androidx.appcompat.app.AppCompatActivity ?: return
        reactInstanceManager?.onHostDestroy(activity)
        reactRootView?.unmountReactApplication()
        reactRootView = null
        reactInstanceManager = null
    }

    override fun invokeDefaultOnBackPressed() {
        val activity = context as? androidx.appcompat.app.AppCompatActivity ?: return
        activity.onBackPressedDispatcher.onBackPressed()
    }
}
