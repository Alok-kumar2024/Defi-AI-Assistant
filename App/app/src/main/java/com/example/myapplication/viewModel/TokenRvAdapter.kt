package com.example.myapplication.viewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.TokenBalance


class TokenRvAdapter(
    val tokenData : MutableList<TokenBalance>,
    val onClick : () -> Unit
) : RecyclerView.Adapter<TokenRvAdapter.HolderViewAdapter>() {

    inner class HolderViewAdapter(itemView : View) : RecyclerView.ViewHolder(itemView){

        val shortName : TextView= itemView.findViewById(R.id.TvCryptoCoinShortName_Market)
        val longName : TextView= itemView.findViewById(R.id.TvCryptoCoinName)
        val tokenImg : ImageView = itemView.findViewById(R.id.IvCryptoCoinImg)
        val increment : TextView = itemView.findViewById(R.id.TvIncrementCryptoCoins_Market)
        val price : TextView = itemView.findViewById(R.id.TvPriceCryptoCoins_Market)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderViewAdapter {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cryptocoin_layout,parent,false)
        return HolderViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return tokenData.size
    }

    override fun onBindViewHolder(holder: HolderViewAdapter, position: Int) {

        val token = tokenData[position]

        holder.shortName.text = token.symbol
        holder.longName.text = token.name

        holder.increment.text = token.changePercent24h.toString()
        holder.price.text = "$${token.priceUsd}"

        Glide.with(holder.itemView.context)
            .load(token.logo)
            .placeholder(R.drawable.bit_coin_vector)
            .error(R.drawable.bit_coin_vector)
            .into(holder.tokenImg)

    }
}