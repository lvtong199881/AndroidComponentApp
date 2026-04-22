package com.mohanlv.reactnative.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.mohanlv.reactnative.R
import com.mohanlv.router.RouterManager

/**
 * RN 管理一级页面 - 功能菜单
 */
class RNManagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rn_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarContainer = view.findViewById<View>(R.id.toolbar_container)

        // 点击关闭按钮
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 点击"本地 Bundle 包管理"进入二级页面
        view.findViewById<View>(R.id.menu_local_bundle).setOnClickListener {
            navigateToLocalBundle()
        }

        // 处理 WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            toolbarContainer.setPadding(0, systemBars.top, 0, 0)
            v.findViewById<View>(R.id.menu_local_bundle).setPadding(
                systemBars.left + 16,
                systemBars.top + 16,
                systemBars.right + 16,
                systemBars.bottom + 16
            )
            insets
        }
    }

    private fun navigateToLocalBundle() {
        val fragment = LocalBundleFragment()
        // 使用 parentFragmentManager 因为当前 Fragment 是被 MainActivity 添加的
        parentFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val TAG = "RNManagerFragment"
    }
}
