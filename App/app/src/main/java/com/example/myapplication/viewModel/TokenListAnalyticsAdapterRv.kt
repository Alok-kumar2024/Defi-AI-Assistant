package com.example.myapplication.viewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.TokenPie

class TokenListAnalyticsAdapterRv(
    private val tokenList : MutableList<TokenPie>
) : RecyclerView.Adapter<TokenListAnalyticsAdapterRv.ViewHolderTokenList>() {

    inner class ViewHolderTokenList(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tokenName : TextView = itemView.findViewById(R.id.tvTokenName_tokenList)
        val tokenValue : TextView = itemView.findViewById(R.id.tvTokenValue_tokenList)
        val tokenPercentage : TextView = itemView.findViewById(R.id.tvTokenPercent_tokenList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTokenList {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.analytics_token_list,parent,false)
        return ViewHolderTokenList(view)
    }

    override fun getItemCount(): Int {
        return tokenList.size
    }

    override fun onBindViewHolder(holder: ViewHolderTokenList, position: Int) {
        val listToken = tokenList[position]

        holder.tokenName.text = listToken.name
        holder.tokenValue.text = "$${String.format("%.2f", listToken.valueUsd)}"
        holder.tokenPercentage.text = "${listToken.sharePercent}%"
    }
}