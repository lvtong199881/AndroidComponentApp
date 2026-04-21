package com.mohanlv.home.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohanlv.base.R
import com.mohanlv.network.model.Article

/**
 * 文章列表适配器
 */
class ArticleAdapter(
    private val articles: List<Article>,
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: android.widget.TextView = itemView.findViewById(R.id.tvTitle)
        val authorText: android.widget.TextView = itemView.findViewById(R.id.tvAuthor)
        val dateText: android.widget.TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.titleText.text = article.title
        holder.authorText.text = article.author.ifEmpty { article.shareUser }
        holder.dateText.text = article.niceDate
        holder.itemView.setOnClickListener { onItemClick(article) }
    }

    override fun getItemCount(): Int = articles.size
}