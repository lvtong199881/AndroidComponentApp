package com.mohanlv.base.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import com.mohanlv.base.R

/**
 * 基础 DialogFragment
 * 支持两种样式：
 * - alert: 居中弹窗
 * - popup: 底部弹窗
 */
abstract class BaseDialogFragment : DialogFragment() {

    enum class Style { ALERT, POPUP }

    /** 弹窗样式，默认底部弹窗 */
    protected open val style: Style = Style.POPUP

    /** 是否启用进入/退出动画 */
    protected open val animated: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            when (style) {
                Style.ALERT -> {
                    setLayout(
                        (resources.displayMetrics.widthPixels * 0.85).toInt(),
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    setGravity(Gravity.CENTER)
                    setDimAmount(0.4f)
                }

                Style.POPUP -> {
                    setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    setGravity(Gravity.BOTTOM)
                    setDimAmount(0.5f)
                }
            }

            if (animated) {
                setWindowAnimations(0) // 禁用默认动画，我们在代码中控制
            } else {
                setWindowAnimations(0)
            }
        }

        // 应用进入动画
        if (animated) {
            dialog?.window?.decorView?.let { view ->
                view.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        getEnterAnimRes()
                    )
                )
            }
        }
    }

    /** 获取布局 ID */
    protected abstract fun getLayoutId(): Int

    /** 获取进入动画资源 */
    protected open fun getEnterAnimRes(): Int = when (style) {
        Style.ALERT -> R.anim.dialog_enter
        Style.POPUP -> R.anim.bottom_sheet_enter
    }

    /** 获取退出动画资源 */
    protected open fun getExitAnimRes(): Int = when (style) {
        Style.ALERT -> R.anim.dialog_exit
        Style.POPUP -> R.anim.bottom_sheet_exit
    }

    /** 关闭弹窗（带动画） */
    protected open fun dismissWithAnimation() {
        if (animated) {
            dialog?.window?.decorView?.let { view ->
                val exitAnim = AnimationUtils.loadAnimation(requireContext(), getExitAnimRes())
                exitAnim.setAnimationListener(object :
                    android.view.animation.Animation.AnimationListener {
                    override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                    override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                        dismiss()
                    }

                    override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                })
                view.startAnimation(exitAnim)
            } ?: dismiss()
        } else {
            dismiss()
        }
    }

    override fun dismiss() {
        if (animated && isAdded && dialog?.window?.decorView != null) {
            dialog?.window?.decorView?.let { view ->
                val exitAnim = AnimationUtils.loadAnimation(requireContext(), getExitAnimRes())
                exitAnim.setAnimationListener(object :
                    android.view.animation.Animation.AnimationListener {
                    override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                    override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                        super@BaseDialogFragment.dismiss()
                    }

                    override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                })
                view.startAnimation(exitAnim)
                return
            }
        }
        super.dismiss()
    }
}
