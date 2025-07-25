package com.example.myapplication.viewModel

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.Article
import com.example.myapplication.model.NewsResponse

class MarketNewsAdapter(
    val newsList : MutableList<Article>
) : RecyclerView.Adapter<MarketNewsAdapter.ViewHolderNews>() {

    inner class ViewHolderNews(itemView : View) : RecyclerView.ViewHolder(itemView){

        val newsTitle : TextView = itemView.findViewById(R.id.TvNewsTitle_CryptoNews)
        val newsBody : TextView = itemView.findViewById(R.id.TvNewsBody_CryptoNews)
        val newsTimeSource : TextView = itemView.findViewById(R.id.TvTimeAndOther_CryptoNews)
        val newsLogo : ImageView = itemView.findViewById(R.id.IVNewsImage_CryptoNews)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderNews {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.crypto_news,parent,false)
        return ViewHolderNews(view)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: ViewHolderNews, position: Int) {
        val news = newsList[position]

        holder.newsTitle.text = news.title
        holder.newsBody.text = news.description
        holder.newsTimeSource.text = "${news.source.name}•${news.publishedAt}"

        Glide.with(holder.itemView.context)
            .load(news.urlToImage)
            .error(R.drawable.error_vector)
            .into(holder.newsLogo)

        Log.d("MarketCoins","The Url of img : ${news.urlToImage}")
    }
}