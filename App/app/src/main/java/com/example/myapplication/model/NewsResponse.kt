package com.example.myapplication.model

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: MutableList<Article>
)

data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val source: Source
)

data class Source(
    val name: String
)
