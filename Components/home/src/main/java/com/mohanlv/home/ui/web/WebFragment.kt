package com.mohanlv.home.ui.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.home.databinding.FragmentWebBinding

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
