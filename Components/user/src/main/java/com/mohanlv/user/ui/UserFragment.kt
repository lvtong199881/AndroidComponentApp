package com.mohanlv.user.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.login.LoginPrefs
import com.mohanlv.login.vm.LoginState
import com.mohanlv.login.vm.OnLoginStateChangedListener
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
class UserFragment : BaseFragment<FragmentUserCenterBinding>(), OnLoginStateChangedListener {

    private val apiService = NetworkManager.createApi(ApiService::class.java)
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserCenterBinding {
        return FragmentUserCenterBinding.inflate(inflater, container, false)
    }

    companion object {
        private const val TAG = "UserFragment"
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        
        // 从 LoginPrefs 恢复登录状态
        if (LoginPrefs.isLogin) {
            LoginState.restore(
                userId = LoginPrefs.userId,
                username = LoginPrefs.username ?: "",
                nickname = LoginPrefs.nickname
            )
        }
    }

    override fun initData() {
        super.initData()
        if (LoginState.isLoggedIn) {
            loadUserCoinInfo()
        }
    }

    override fun initEvent() {
        binding.btnLogout.setOnClickListener { logout() }
        binding.btnLogin.setOnClickListener { RouterManager.navigate(RoutePath.LOGIN, addToBackStack = true) }
        binding.layoutCollect.setOnClickListener { RouterManager.navigate(RoutePath.COLLECT_LIST, addToBackStack = true) }
    }

    /**
     * 更新用户信息显示
     */
    private fun updateUserInfo() {
        if (LoginState.isLoggedIn) {
            binding.layoutLoggedIn.visibility = View.VISIBLE
            binding.layoutNotLogin.visibility = View.GONE

            binding.tvUsername.text = LoginState.nickname.ifEmpty { LoginState.username }
            binding.tvUserId.text = getString(R.string.user_id_format, LoginState.userId)
            binding.tvAvatar.text = binding.tvUsername.text.firstOrNull()?.uppercase() ?: "?"
        } else {
            binding.layoutLoggedIn.visibility = View.GONE
            binding.layoutNotLogin.visibility = View.VISIBLE
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
                        binding.tvCoinCount.text = userInfo?.coinCount?.toString() ?: getString(R.string.default_placeholder)
                        binding.tvRank.text = getString(R.string.rank_format, userInfo?.rank ?: getString(R.string.default_placeholder))
                        Log.i(TAG, "积分信息加载成功: ${userInfo?.coinCount}")
                    } else {
                        Log.e(TAG, "获取积分失败: ${body?.errorMsg}")
                        if (body?.needLogin() == true) {
                            clearLoginState()
                            return@launch
                        }
                        binding.tvCoinCount.text = getString(R.string.default_placeholder)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取积分失败", e)
                binding.tvCoinCount.text = getString(R.string.default_placeholder)
            }
        }
    }

    private fun clearLoginState() {
        LoginState.clear()
    }

    private fun logout() {
        LoginState.logout()
        Toast.makeText(context, R.string.logout_success, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        LoginState.addListener(this)
        updateUserInfo()
        if (LoginState.isLoggedIn) {
            loadUserCoinInfo()
        }
    }

    override fun onPause() {
        super.onPause()
        LoginState.removeListener(this)
    }

    override fun onLoginSuccess(userId: Int, username: String, nickname: String?) {
        updateUserInfo()
        loadUserCoinInfo()
    }

    override fun onLogout() {
        updateUserInfo()
    }
}