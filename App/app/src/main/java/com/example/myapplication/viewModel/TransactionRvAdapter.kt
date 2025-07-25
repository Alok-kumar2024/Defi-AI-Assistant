package com.example.myapplication.viewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.Transaction

class TransactionRvAdapter(
    val transactionData : MutableList<Transaction>
) : RecyclerView.Adapter<TransactionRvAdapter.ViewHolderTransaction>() {

    inner class ViewHolderTransaction(itemView : View) : RecyclerView.ViewHolder(itemView){

        val sendIv : ImageView = itemView.findViewById(R.id.IvSend_Receive_Transaction)
        val SendTv : TextView = itemView.findViewById(R.id.TvSend_Transaction)
        val AssetTypeTv : TextView = itemView.findViewById(R.id.TvAssetsType_Transaction)
        val addressTv : TextView = itemView.findViewById(R.id.TvAddress_Transaction)
        val timeTv : TextView = itemView.findViewById(R.id.TvTime_Transaction)
        val tokenAmountTv : TextView = itemView.findViewById(R.id.TvTokenAmount_Transaction)
        val tokenLogoIv : ImageView = itemView.findViewById(R.id.IvTokenLogo_Transaction)
        val tokenPriceTv : TextView = itemView.findViewById(R.id.TvPrice_Transaction)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTransaction {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.crypto_transaction,parent,false)
        return  ViewHolderTransaction(view)
    }

    override fun getItemCount(): Int {
        return transactionData.size
    }

    override fun onBindViewHolder(holder: ViewHolderTransaction, position: Int) {
        val transaction = transactionData[position]

        holder.SendTv.text = transaction.type

        if (transaction.type == "send"){
            holder.sendIv.setImageResource(R.drawable.send_transaction_vector)

            holder.addressTv.text = transaction.toAddress

            holder.tokenAmountTv.text = "-${transaction.amount} ${transaction.name}"

        }else{
            holder.sendIv.setImageResource(R.drawable.receive_transaction_vector)

            holder.addressTv.text = transaction.fromAddress

            holder.tokenAmountTv.text = "+${transaction.amount} ${transaction.name}"
        }

        holder.AssetTypeTv.text = transaction.assetType


        holder.timeTv.text = transaction.timestamp

        Glide.with(holder.itemView.context)
            .load(transaction.logo)
            .placeholder(R.drawable.bit_coin_vector)
            .error(R.drawable.confirm_password_vector)
            .into(holder.tokenLogoIv)

        holder.tokenPriceTv.text = "$${transaction.amountUsd}"

    }
}