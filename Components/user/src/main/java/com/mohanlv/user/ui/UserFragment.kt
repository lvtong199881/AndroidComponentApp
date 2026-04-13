package com.mohanlv.user.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.base.utils.SPUtils
import com.mohanlv.login.vm.LoginState
import com.mohanlv.network.NetworkManager
import com.mohanlv.network.api.ApiService
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import com.mohanlv.user.databinding.FragmentUserBinding
import kotlinx.coroutines.launch

/**
 * 个人中心页面
 * 显示用户信息和积分
 */
@Route(path = RoutePath.USER, description = "个人中心")
class UserFragment : BaseFragment<FragmentUserBinding>() {

    private val apiService = NetworkManager.createApi(ApiService::class.java)

    companion object {
        private const val TAG = "UserFragment"
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserBinding {
        return FragmentUserBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        updateUserInfo()
    }

    override fun initData() {
        super.initData()
        // 检查登录状态
        if (!LoginState.isLoggedIn) {
            // 未登录，跳转到登录页
            RouterManager.navigate(RoutePath.LOGIN)
            return
        }

        // 加载用户积分信息
        loadUserCoinInfo()
    }

    override fun initEvent() {
        // 退出登录按钮
        binding.btnLogout.setOnClickListener {
            logout()
        }

        // 登录按钮（未登录时显示）
        binding.btnLogin.setOnClickListener {
            RouterManager.navigate(RoutePath.LOGIN)
        }
    }

    /**
     * 更新用户信息显示
     */
    private fun updateUserInfo() {
        if (LoginState.isLoggedIn) {
            // 已登录状态
            binding.layoutLoggedIn.visibility = View.VISIBLE
            binding.layoutNotLogin.visibility = View.GONE
            binding.btnLogout.visibility = View.VISIBLE

            binding.tvUsername.text = LoginState.nickname.ifEmpty { LoginState.username }
            binding.tvUserId.text = "ID: ${LoginState.userId}"
        } else {
            // 未登录状态
            binding.layoutLoggedIn.visibility = View.GONE
            binding.layoutNotLogin.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.GONE
        }
    }

    /**
     * 加载用户积分信息
     */
    private fun loadUserCoinInfo() {
        if (!LoginState.isLoggedIn) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getCoinInfo()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess()) {
                        val userInfo = body.data
                        binding.tvCoinCount.text = "${userInfo?.coinCount ?: 0}"
                        binding.tvRank.text = "排名: ${userInfo?.rank ?: "--"}"
                        Log.i(TAG, "积分信息加载成功: ${userInfo?.coinCount}")
                    } else {
                        Log.e(TAG, "获取积分失败: ${body?.errorMsg}")
                        binding.tvCoinCount.text = "--"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取积分失败: ${e.message}")
                binding.tvCoinCount.text = "--"
            }
        }
    }

    /**
     * 退出登录
     */
    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 调用退出接口
                apiService.logout()
            } catch (e: Exception) {
                // 忽略退出接口错误
            }

            // 清除本地登录状态
            LoginState.clear()
            SPUtils.clear()

            Toast.makeText(context, "已退出登录", Toast.LENGTH_SHORT).show()

            // 刷新界面
            updateUserInfo()
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次回到页面时刷新状态
        updateUserInfo()
        if (LoginState.isLoggedIn) {
            loadUserCoinInfo()
        }
    }
}
