package com.example.myapplication.viewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.ChatMessages

class AI_adapter(
    val chatHistory : MutableList<ChatMessages>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(chatHistory[position].role){
            "user" -> 0
            "assistant" -> 1
            else -> 2
        }
    }

    inner class UserChat(item : View) : RecyclerView.ViewHolder(item){
        val msgUser : TextView = item.findViewById(R.id.UserText)

    }

    inner class BotChat(item : View) : RecyclerView.ViewHolder(item){
        val botmsg : TextView = item.findViewById(R.id.Bot_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType ==0){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_layout,parent,false)
            UserChat(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.ai_response_layout,parent,false)
            BotChat(view)
        }
    }

    override fun getItemCount(): Int {
        return chatHistory.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = chatHistory[position]

        if (holder is UserChat){
            holder.msgUser.text = chat.content
        }else if(holder is BotChat){
            holder.botmsg.text = chat.content
        }
    }
}