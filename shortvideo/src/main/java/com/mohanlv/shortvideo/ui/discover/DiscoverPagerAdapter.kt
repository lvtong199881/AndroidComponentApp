package com.mohanlv.shortvideo.ui.discover

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mohanlv.router.RouterManager
import com.mohanlv.shortvideo.ui.common.EmptyFragment

/**
 * 发现页面 ViewPager 适配器
 */
class DiscoverPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments: MutableList<Fragment> = mutableListOf(
        RouterManager.getFragment("oneandroid://shortvideo/photos") ?: EmptyFragment(),
        RouterManager.getFragment("oneandroid://shortvideo/videos") ?: EmptyFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}