package com.mohanlv.shortvideo.ui.search

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mohanlv.shortvideo.R

/**
 * 筛选面板弹窗
 */
class FilterPanelPopup(
    context: Context,
    private var currentOrientation: String,
    private var currentSize: String,
    private var currentColor: String,
    private val onReset: () -> Unit,
    private val onConfirm: (orientation: String, size: String, color: String) -> Unit
) : PopupWindow(context) {

    private val ctx: Context = context
    private var tempOrientation = currentOrientation
    private var tempSize = currentSize
    private var tempColor = currentColor

    private val chipGroupOrientation: ChipGroup
    private val chipGroupSize: ChipGroup
    private val chipGroupColor: ChipGroup

    init {
        val view = View.inflate(ctx, R.layout.panel_filter, null)

        chipGroupOrientation = view.findViewById(R.id.chipGroupOrientation)
        chipGroupSize = view.findViewById(R.id.chipGroupSize)
        chipGroupColor = view.findViewById(R.id.chipGroupColor)

        val btnReset = view.findViewById<View>(R.id.btnReset)
        val btnConfirm = view.findViewById<View>(R.id.btnConfirm)

        setupChips()

        btnReset.setOnClickListener {
            tempOrientation = ""
            tempSize = ""
            tempColor = ""
            setupChips()
            onReset()
        }

        btnConfirm.setOnClickListener {
            currentOrientation = tempOrientation
            currentSize = tempSize
            currentColor = tempColor
            dismiss()
            onConfirm(currentOrientation, currentSize, currentColor)
        }

        contentView = view
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        elevation = 8f
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
        animationStyle = R.style.Animation_Panel
        inputMethodMode = INPUT_METHOD_NOT_NEEDED
        softInputMode = INPUT_METHOD_NOT_NEEDED
    }

    fun show(anchor: View) {
        tempOrientation = currentOrientation
        tempSize = currentSize
        tempColor = currentColor
        setupChips()
        showAsDropDown(anchor)
    }

    private fun setupChips() {
        chipGroupOrientation.removeAllViews()
        chipGroupSize.removeAllViews()
        chipGroupColor.removeAllViews()

        addChip(chipGroupOrientation, R.string.filter_orientation_landscape, "landscape", tempOrientation) { tempOrientation = it }
        addChip(chipGroupOrientation, R.string.filter_orientation_portrait, "portrait", tempOrientation) { tempOrientation = it }
        addChip(chipGroupOrientation, R.string.filter_orientation_square, "square", tempOrientation) { tempOrientation = it }

        addChip(chipGroupSize, R.string.filter_size_large, "large", tempSize) { tempSize = it }
        addChip(chipGroupSize, R.string.filter_size_medium, "medium", tempSize) { tempSize = it }
        addChip(chipGroupSize, R.string.filter_size_small, "small", tempSize) { tempSize = it }

        addChip(chipGroupColor, R.string.filter_color_black, "black", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_black_white, "black_and_white", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_red, "red", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_orange, "orange", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_yellow, "yellow", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_green, "green", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_turquoise, "turquoise", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_blue, "blue", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_violet, "violet", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_pink, "pink", tempColor) { tempColor = it }
        addChip(chipGroupColor, R.string.filter_color_brown, "brown", tempColor) { tempColor = it }
    }

    private fun addChip(chipGroup: ChipGroup, labelRes: Int, value: String, currentValue: String, onSelected: (String) -> Unit) {
        val chip = Chip(ctx).apply {
            text = ctx.getString(labelRes)
            isCheckable = true
            isChecked = value == currentValue
            chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                if (value == currentValue) Color.parseColor("#2196F3") else Color.parseColor("#1F000000")
            )
            setTextColor(if (value == currentValue) Color.WHITE else ctx.getColor(R.color.text_primary))
            isCloseIconVisible = value == currentValue
            closeIconTint = android.content.res.ColorStateList.valueOf(Color.WHITE)
            setCloseIconResource(R.drawable.ic_chip_close)
            isCheckedIconVisible = false
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    for (i in 0 until chipGroup.childCount) {
                        val otherChip = chipGroup.getChildAt(i) as? Chip
                        if (otherChip != null && otherChip != this) {
                            otherChip.isChecked = false
                            otherChip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(Color.parseColor("#1F000000"))
                            otherChip.setTextColor(ctx.getColor(R.color.text_primary))
                            otherChip.isCloseIconVisible = false
                        }
                    }
                    chipBackgroundColor = android.content.res.ColorStateList.valueOf(Color.parseColor("#2196F3"))
                    setTextColor(Color.WHITE)
                    isCloseIconVisible = true
                    onSelected(value)
                }
            }
        }
        chipGroup.addView(chip)
    }
}