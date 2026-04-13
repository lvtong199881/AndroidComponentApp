package com.mohanlv.user.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.mohanlv.user.R
import com.mohanlv.user.databinding.FragmentUserCenterBinding
import kotlinx.coroutines.launch

/**
 * 个人中心页面
 * 显示用户信息和积分
 */
@Route(path = RoutePath.USER, description = "个人中心")
class UserFragment : BaseFragment<FragmentUserCenterBinding>() {

    private val apiService = NetworkManager.createApi(ApiService::class.java)

    companion object {
        private const val TAG = "UserFragment"
    }

    // 使用 findViewById 替代 ViewBinding 避免 ID 命名空间问题
    private lateinit var layoutLoggedIn: LinearLayout
    private lateinit var layoutNotLogin: LinearLayout
    private lateinit var tvUsername: TextView
    private lateinit var tvUserId: TextView
    private lateinit var tvCoinCount: TextView
    private lateinit var tvRank: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnLogin: Button

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserCenterBinding {
        return FragmentUserCenterBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        
        // 从 SPUtils 恢复登录状态
        if (SPUtils.isLogin) {
            LoginState.restore(
                userId = SPUtils.userId?.toIntOrNull() ?: 0,
                username = SPUtils.username ?: "",
                nickname = SPUtils.nickname
            )
        }
        
        // 初始化视图引用
        layoutLoggedIn = binding.root.findViewById(R.id.layoutLoggedIn)
        layoutNotLogin = binding.root.findViewById(R.id.layoutNotLogin)
        tvUsername = binding.root.findViewById(R.id.tvUsername)
        tvUserId = binding.root.findViewById(R.id.tvUserId)
        tvCoinCount = binding.root.findViewById(R.id.tvCoinCount)
        tvRank = binding.root.findViewById(R.id.tvRank)
        btnLogout = binding.root.findViewById(R.id.btnLogout)
        btnLogin = binding.root.findViewById(R.id.btnLogin)
        
        updateUserInfo()
    }

    override fun initData() {
        super.initData()
        if (LoginState.isLoggedIn) {
            loadUserCoinInfo()
        }
    }

    override fun initEvent() {
        // 退出登录按钮
        btnLogout.setOnClickListener {
            logout()
        }

        // 登录按钮（未登录时显示）
        btnLogin.setOnClickListener {
            RouterManager.navigate(RoutePath.LOGIN)
        }
    }

    /**
     * 更新用户信息显示
     */
    private fun updateUserInfo() {
        if (LoginState.isLoggedIn) {
            layoutLoggedIn.visibility = View.VISIBLE
            layoutNotLogin.visibility = View.GONE
            btnLogout.visibility = View.VISIBLE

            tvUsername.text = LoginState.nickname.ifEmpty { LoginState.username }
            tvUserId.text = "ID: ${LoginState.userId}"
        } else {
            layoutLoggedIn.visibility = View.GONE
            layoutNotLogin.visibility = View.VISIBLE
            btnLogout.visibility = View.GONE
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
                        tvCoinCount.text = "${userInfo?.coinCount ?: 0}"
                        tvRank.text = "排名: ${userInfo?.rank ?: "--"}"
                        Log.i(TAG, "积分信息加载成功: ${userInfo?.coinCount}")
                    } else {
                        Log.e(TAG, "获取积分失败: ${body?.errorMsg}")
                        tvCoinCount.text = "--"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取积分失败: ${e.message}")
                tvCoinCount.text = "--"
            }
        }
    }

    /**
     * 退出登录
     */
    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                apiService.logout()
            } catch (e: Exception) {
                // 忽略退出接口错误
            }

            LoginState.clear()
            SPUtils.clear()

            Toast.makeText(context, "已退出登录", Toast.LENGTH_SHORT).show()
            updateUserInfo()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserInfo()
        if (LoginState.isLoggedIn) {
            loadUserCoinInfo()
        }
    }
}
