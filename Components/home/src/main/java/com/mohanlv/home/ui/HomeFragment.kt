package com.mohanlv.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.home.databinding.FragmentHomeBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route

@Route(path = RoutePath.HOME, description = "首页")
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    
    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }
}
