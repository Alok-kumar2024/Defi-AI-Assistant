package com.example.myapplication.viewModel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

data class ConnectedwalletData(
    val walletImg : String? = null,
    val walletName : String,
    val walletAddress : String,
    val choosen : Boolean = false
)

class ConnectedWalletAdapter(
    val walletList : MutableList<ConnectedwalletData>,
    val onClick : (ConnectedwalletData) -> Unit
) : RecyclerView.Adapter<ConnectedWalletAdapter.viewHolderConnect>() {

    inner class viewHolderConnect(item : View) : RecyclerView.ViewHolder(item){
        val img : ImageView = item.findViewById(R.id.IvWalletIcon_WalletList)
        val name : TextView = item.findViewById(R.id.TvWalletName_WalletList)
        val address : TextView = item.findViewById(R.id.TvWalletAddress_WalletList)
        val choosen : ImageView = item.findViewById(R.id.IvChoosenWallet_Wallet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolderConnect {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_list_layout,parent,false)
        return viewHolderConnect(view)
    }

    override fun getItemCount(): Int {
        return walletList.size
    }

    override fun onBindViewHolder(holder: viewHolderConnect, position: Int) {
        val list = walletList[position]

        if (list.walletImg ==  null){
            holder.img.setImageResource(R.drawable.wallet_vector)
        }else{
            Glide.with(holder.itemView.context)
                .load(list.walletImg)
                .placeholder(R.drawable.wallet_vector)
                .error(R.drawable.wallet_vector)
                .into(holder.img)
        }

        holder.name.text = list.walletName

        holder.address.text = shortString(list.walletAddress,4,4)

        holder.address.setOnClickListener {
            val clipBoard = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Address",list.walletAddress)
            clipBoard.setPrimaryClip(clip)

            Toast.makeText(holder.itemView.context,"Address Copied..",Toast.LENGTH_SHORT).show()

        }

        if (list.choosen){
            holder.choosen.visibility = View.VISIBLE
        }else{
            holder.choosen.visibility = View.GONE
        }
    }

    fun shortString(text : String , front : Int , back : Int) : String{
        return if (text.length > (front+back+3)){
            val textfront = text.take(front)
            val textBack = text.takeLast(back)

            "$textfront...$textBack"
        }else{
            text
        }
    }


}