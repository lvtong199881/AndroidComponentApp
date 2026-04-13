package com.mohanlv.login.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.base.utils.SPUtils
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import com.mohanlv.login.databinding.FragmentLoginBinding
import com.mohanlv.login.model.LoginResult
import com.mohanlv.login.vm.LoginState
import com.mohanlv.login.vm.LoginViewModel

@Route(path = RoutePath.LOGIN, description = "登录页面")
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

    companion object {
        private const val TAG = "LoginFragment"
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Log.i(TAG, "登录成功: username=${result.username}, userId=${result.userId}")

                    // 保存登录状态
                    LoginState.saveLogin(
                        userId = result.userId,
                        username = result.username,
                        nickname = result.nickname,
                        token = result.token
                    )

                    // 同时保存到 SPUtils（兼容）
                    SPUtils.isLogin = true
                    SPUtils.userId = result.userId.toString()
                    SPUtils.username = result.username

                    showError("登录成功，欢迎 ${result.nickname ?: result.username}")
                    // 跳转到首页
                    RouterManager.navigate(RoutePath.HOME_CONTAINER)
                }

                is LoginResult.Error -> {
                    Log.e(TAG, "登录失败: ${result.message}")
                    showError(result.message)
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    override fun initEvent() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                showError("请输入用户名和密码")
                return@setOnClickListener
            }

            viewModel.login(username, password)
        }
    }

    override fun showError(msg: String) {
        super.showError(msg)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
