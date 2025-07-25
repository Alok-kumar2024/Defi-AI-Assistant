package com.example.myapplication.viewModel

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.example.myapplication.R
import com.example.myapplication.model.Coin
import com.walletconnect.android.internal.common.explorer.data.model.Colors
import org.bouncycastle.asn1.x509.Holder

class marketCoinsAdapter(
    val coinsList : MutableList<Coin>
) : RecyclerView.Adapter<marketCoinsAdapter.ViewHolderCoin>() {

    inner class ViewHolderCoin(itemView : View) : RecyclerView.ViewHolder(itemView){
        val coinLogo : ImageView = itemView.findViewById(R.id.IvCryptoCoinImg_Market)
        val coinName : TextView = itemView.findViewById(R.id.TvCrptoCoinName_Market)
        val coinSymbol : TextView = itemView.findViewById(R.id.TvCryptoCoinShortName_Market)
        val coinPrice : TextView = itemView.findViewById(R.id.TvPriceCryptoCoins_Market)
        val coinIncrement : TextView = itemView.findViewById(R.id.TvIncrementCryptoCoins_Market)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCoin {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.cryptocoin_market,parent,false)
        return ViewHolderCoin(view)
    }

    override fun getItemCount(): Int {
        return coinsList.size
    }

    override fun onBindViewHolder(holder: ViewHolderCoin, position: Int) {
        val listcoin = coinsList[position]

        holder.coinName.text = listcoin.name
        holder.coinPrice.text = "$${listcoin.current_price}"
        holder.coinSymbol.text = listcoin.symbol
        if (listcoin.price_change_percentage_24h < 0){
            holder.coinIncrement.setTextColor(Color.RED)
            holder.coinIncrement.text = "${String.format("%.2f",listcoin.price_change_percentage_24h)}%"
        }else{
            holder.coinIncrement.setTextColor(Color.GREEN)
            holder.coinIncrement.text = "+${String.format("%.2f",listcoin.price_change_percentage_24h)}%"
        }

        Glide.with(holder.itemView.context)
            .load(listcoin.image)
            .override(100,100)
            .error(R.drawable.error_vector)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.coinLogo)

        Log.d("MarketCoins","The Url of img : ${listcoin.image}")

        // have to change somethings , like for increment how to know if it +  or - , also have to make adapter of news ,
    // and the use in market, also what out top gainer and loser ?? how to get them ??
    }
}