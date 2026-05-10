package com.mohanlv.shortvideo.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updatePadding
import com.google.android.material.tabs.TabLayoutMediator
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.base.utils.getStatusBarHeight
import com.mohanlv.common.getSafeContext
import com.mohanlv.router.annotation.Route
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.databinding.FragmentDiscoverBinding

/**
 * 发现页面
 * 包含两个 Tab：精选照片、热门视频
 * 使用 TabLayout + ViewPager2 实现
 */
@Route(path = "oneandroid://shortvideo/discover", description = "发现")
class DiscoverFragment : BaseFragment<FragmentDiscoverBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiscoverBinding {
        return FragmentDiscoverBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.tabLayout.updatePadding(top = getStatusBarHeight(getSafeContext()))
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = DiscoverPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // 关联 TabLayout 和 ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_photos)
                1 -> getString(R.string.tab_videos)
                else -> ""
            }
        }.attach()
    }
}