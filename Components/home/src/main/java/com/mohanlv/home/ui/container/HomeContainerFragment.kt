package com.mohanlv.home.ui.container

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.home.R
import com.mohanlv.home.databinding.FragmentHomeContainerBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route

@Route(path = RoutePath.HOME_CONTAINER, description = "首页容器")
class HomeContainerFragment : BaseFragment<FragmentHomeContainerBinding>() {

    private val fragments = mutableMapOf<String, Fragment>()
    private var currentFragmentTag: String? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeContainerBinding {
        return FragmentHomeContainerBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // 直接使用 XML 中定义好的 menu
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val tag = when (item.itemId) {
                R.id.nav_home -> "home"
                R.id.nav_web -> "web"
                R.id.nav_user -> "user"
                else -> return@setOnItemSelectedListener false
            }
            switchFragment(tag)
            true
        }

        // 默认显示第一个
        switchFragment("home")
    }

    private fun switchFragment(tag: String) {
        if (currentFragmentTag == tag) return

        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // 隐藏当前 Fragment
        currentFragmentTag?.let { currentTag ->
            fragments[currentTag]?.let { transaction.hide(it) }
        }

        // 显示或添加目标 Fragment
        var targetFragment = fragments[tag]
        if (targetFragment == null) {
            targetFragment = createFragment(tag)
            if (targetFragment != null) {
                fragments[tag] = targetFragment
                transaction.add(R.id.fragmentContainer, targetFragment, tag)
            }
        } else {
            transaction.show(targetFragment)
        }

        transaction.commit()
        currentFragmentTag = tag
    }

    private fun createFragment(tag: String): Fragment? {
        return when (tag) {
            "home" -> com.mohanlv.home.ui.HomeFragment()
            "web" -> com.mohanlv.home.ui.web.WebFragment()
            "user" -> com.mohanlv.user.ui.UserFragment()
            else -> null
        }
    }
}
