package com.mohanlv.websdk.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebSettings
import android.view.KeyEvent
import android.webkit.WebView
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.websdk.R
import com.mohanlv.websdk.databinding.FragmentWebBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route

@Route(path = "oneandroid://common/webview", description = "网页")
class WebFragment : BaseFragment<FragmentWebBinding>() {

    companion object {
        private const val DEFAULT_URL = "https://www.baidu.com"
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWebBinding {
        return FragmentWebBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupWebView()

        // 获取 URL 参数
        arguments?.getString("url")?.let { url ->
            binding.webView.loadUrl(url)
        }
    }

    override fun initEvent() {
        super.initEvent()
        // 拦截 WebView 的返回键
        binding.webView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }

    private fun setupWebView() {
        binding.webView.apply {
            settings.apply {
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            loadUrl(DEFAULT_URL)
        }
    }

    override fun onDestroyView() {
        binding.webView.apply {
            stopLoading()
            clearHistory()
            removeAllViews()
            destroy()
        }
        super.onDestroyView()
    }
}
