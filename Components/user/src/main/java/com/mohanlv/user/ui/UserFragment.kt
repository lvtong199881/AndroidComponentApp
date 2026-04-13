package com.mohanlv.user.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.user.databinding.FragmentUserBinding
import com.mohanlv.router.RoutePath
import com.mohanlv.router.annotation.Route

@Route(path = RoutePath.USER, description = "个人中心")
class UserFragment : BaseFragment<FragmentUserBinding>() {
    
    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserBinding {
        return FragmentUserBinding.inflate(inflater, container, false)
    }
}
