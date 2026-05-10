package com.mohanlv.shortvideo.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.databinding.FragmentEmptyBinding

/**
 * 空页面 Fragment，用于路由未找到时降级显示
 */
class EmptyFragment : Fragment() {

    private var _binding: FragmentEmptyBinding? = null
    private val binding: FragmentEmptyBinding?
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmptyBinding.inflate(inflater, container, false)
        return _binding?.root ?: View(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}