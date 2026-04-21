package com.mohanlv.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.base.utils.SPUtils
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route
import com.mohanlv.login.databinding.FragmentLoginBinding
import com.mohanlv.login.model.LoginResult
import com.mohanlv.login.vm.LoginState
import com.mohanlv.login.vm.LoginViewModel

@Route(path = RoutePath.LOGIN, description = "登录页面")
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel: LoginViewModel by viewModels()

    private val loginHistory = mutableListOf<SPUtils.Account>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        observeViewModel()
        loadLoginHistory()
    }

    private fun loadLoginHistory() {
        val history = SPUtils.loginHistory
        loginHistory.clear()
        loginHistory.addAll(history)
        binding.chipGroupHistory.removeAllViews()
        for (account in loginHistory) {
            val chip = Chip(requireContext()).apply {
                text = account.username
                isClickable = true
                setOnClickListener {
                    binding.etUsername.setText(account.username)
                    binding.etPassword.setText(account.password)
                    viewModel.login(account.username, account.password)
                }
            }
            binding.chipGroupHistory.addView(chip)
        }
    }

    private fun saveToHistory(username: String, password: String) {
        loginHistory.removeAll { it.username == username }
        loginHistory.add(0, SPUtils.Account(username, password))
        if (loginHistory.size > 5) {
            loginHistory.removeAt(loginHistory.lastIndex)
        }
        SPUtils.loginHistory = loginHistory.toList()
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    saveToHistory(result.username, binding.etPassword.text.toString())
                    LoginState.saveLogin(
                        userId = result.userId,
                        username = result.username,
                        nickname = result.nickname,
                        token = result.token
                    )
                    SPUtils.isLogin = true
                    SPUtils.userId = result.userId
                    SPUtils.username = result.username
                    showError("登录成功，欢迎 ${result.nickname ?: result.username}")
                    parentFragmentManager.popBackStack()
                }

                is LoginResult.Error -> {
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
