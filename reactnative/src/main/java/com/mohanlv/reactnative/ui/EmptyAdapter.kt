package com.mohanlv.reactnative.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.reactnative.R

/**
 * 空状态适配器
 */
class EmptyAdapter : RecyclerView.Adapter<EmptyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.tv_empty)

        init {
            textView.text = view.context.getString(R.string.bundle_empty)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bundle_empty, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 无数据时不需要绑定
    }

    override fun getItemCount(): Int = 1
}