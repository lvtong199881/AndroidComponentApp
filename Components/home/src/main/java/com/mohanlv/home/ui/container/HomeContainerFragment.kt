package com.mohanlv.home.ui.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.home.R
import com.mohanlv.home.databinding.FragmentHomeContainerBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route

@Route(path = RoutePath.HOME_CONTAINER, description = "首页容器")
class HomeContainerFragment : BaseFragment<FragmentHomeContainerBinding>() {

    private val fragments = mutableMapOf<String, Fragment>()
    private var currentFragmentTag: String? = null

    companion object {
        private const val KEY_SELECTED_TAG = "selected_tag"
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeContainerBinding {
        return FragmentHomeContainerBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupBottomNavigation()

        // 恢复保存的选中状态（避免 popBackStack 后状态丢失）
        savedInstanceState?.getString(KEY_SELECTED_TAG)?.let { tag ->
            currentFragmentTag = tag
            val menuItemId = when (tag) {
                "home" -> R.id.nav_home
                "web" -> R.id.nav_web
                "user" -> R.id.nav_user
                "rn" -> R.id.nav_rn
                else -> R.id.nav_home
            }
            binding.bottomNavigation.selectedItemId = menuItemId
            // 恢复 Fragment 显示状态
            fragments[tag]?.let {
                childFragmentManager.beginTransaction().show(it).commit()
            }
            return
        }

        switchFragment("home")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentFragmentTag?.let { outState.putString(KEY_SELECTED_TAG, it) }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val tag = when (item.itemId) {
                R.id.nav_home -> "home"
                R.id.nav_web -> "web"
                R.id.nav_user -> "user"
                R.id.nav_rn -> "rn"
                else -> return@setOnItemSelectedListener false
            }
            switchFragment(tag)
            true
        }

        switchFragment("home")
    }

    private fun switchFragment(tag: String) {
        if (currentFragmentTag == tag) return

        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()

        currentFragmentTag?.let { currentTag ->
            fragments[currentTag]?.let { transaction.hide(it) }
        }

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
            "user" -> RouterManager.getFragment(RoutePath.USER)
            "rn" -> {
                val bundle = android.os.Bundle()
                bundle.putString("repo", "MyRNApp")
                bundle.putString("componentName", "MyRNApp")
                com.mohanlv.reactnative.ui.RNFragment().apply { arguments = bundle }
            }
            else -> null
        }
    }
}