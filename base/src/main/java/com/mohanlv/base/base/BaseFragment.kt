package com.mohanlv.base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.mohanlv.base.utils.log

/**
 * 基础 Fragment
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    companion object {
        const val TAG = "BaseFragment"
    }

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initData()
        initEvent()
    }

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    protected open fun initView(savedInstanceState: Bundle?) {}
    protected open fun initData() {}
    protected open fun initEvent() {
        log("BaseFragment::initEvent")
    }

    protected open fun showLoading() {}
    protected open fun hideLoading() {}
    protected open fun showError(msg: String) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
