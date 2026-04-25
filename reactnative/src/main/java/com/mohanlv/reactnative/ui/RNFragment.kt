package com.mohanlv.reactnative.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mohanlv.reactnative.BundleConfig
import com.mohanlv.reactnative.BundleUrl
import com.mohanlv.reactnative.databinding.FragmentRnBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route

/**
 * React Native 页面容器 Fragment
 */
@Route(path = "oneandroid://common/rn", description = "React Native 页面")
class RNFragment : Fragment() {

    private var viewBinding: FragmentRnBinding? = null

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
        
        val config = BundleConfig(
            repo = repo,
            version = version,
            buildType = buildType
        )
        
        val extraProps = Bundle().apply {
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
        
        viewBinding?.rnView?.startLoading(config, extraProps)
    }

    private fun closeFragment() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.remove(this)
            ?.commitAllowingStateLoss()
    }
    
    override fun onResume() {
        super.onResume()
        viewBinding?.rnView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewBinding?.rnView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding?.rnView?.onDestroy()
        viewBinding = null
    }
}
