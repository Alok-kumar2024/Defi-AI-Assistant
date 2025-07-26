package com.example.myapplication.viewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.view.HomeFragment
import com.example.myapplication.R

class Wallet_ListAdapter(
    val listData : MutableList<HomeFragment.WalletItem>,
    val onClick : (HomeFragment.WalletItem) -> Unit
) : RecyclerView.Adapter<Wallet_ListAdapter.viewHolderAdapter>() {

    inner class viewHolderAdapter(item : View) : RecyclerView.ViewHolder(item){
        val img : ImageView = item.findViewById(R.id.IVWalletListRv_Img)
        val nameWallet : TextView = item.findViewById(R.id.TvWalletName_WalletListRv)
        val layoutLL : LinearLayout = item.findViewById(R.id.LLWalletListRv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolderAdapter {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_list_rv,parent,false)
        return viewHolderAdapter(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: viewHolderAdapter, position: Int) {

        val list = listData[position]

        holder.nameWallet.text = list.name

        Glide.with(holder.itemView.context)
            .load(list.image)
            .placeholder(R.drawable.wallet_vector)
            .error(R.drawable.error_vector)
            .into(holder.img)

        holder.layoutLL.setOnClickListener {
            onClick(list)
        }

    }

}