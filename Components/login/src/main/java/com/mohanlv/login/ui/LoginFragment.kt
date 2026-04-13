package com.mohanlv.login.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.viewModels
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.base.utils.SPUtils
import com.mohanlv.login.databinding.FragmentLoginBinding
import com.mohanlv.login.model.LoginResult
import com.mohanlv.login.vm.LoginViewModel
import com.mohanlv.router.RoutePath
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route

@Route(path = RoutePath.LOGIN, description = "登录页面")
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

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
                    Log.i("mhs", "登录成功: token=${result.token}, userId=${result.userId}")
                    // 保存登录状态
                    SPUtils.token = result.token
                    SPUtils.userId = result.userId
                    SPUtils.isLogin = true
                    showError("登录成功")
                    // 跳转到 HomeFragment
                    RouterManager.navigate(RoutePath.HOME)
                }
                is LoginResult.Error -> {
                    Log.i("mhs", "登录失败: ${result.message}")
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
        Log.i("mhs", "initEvent")
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            if (username.isBlank() || password.isBlank()) {
                Log.i("mhs", "xxxxxxx")
                showError("请输入用户名和密码")
                return@setOnClickListener
            }
            viewModel.login(username, password)
        }
    }

    override fun showError(msg: String) {
        super.showError(msg)
        Toast.makeText(context, msg, LENGTH_SHORT).show()
    }
}
