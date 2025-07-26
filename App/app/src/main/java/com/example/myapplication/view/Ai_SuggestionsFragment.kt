package com.example.myapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAiSuggestionsBinding
import com.example.myapplication.model.ChatMessages
import com.example.myapplication.model.ThemeHelper
import com.example.myapplication.viewModel.AI_Suggestion_ViewModel
import com.example.myapplication.viewModel.AI_adapter
import kotlinx.coroutines.launch

class Ai_SuggestionsFragment : Fragment() {

    private var _binding : FragmentAiSuggestionsBinding? = null
    private val bindning get() = _binding!!

    private var chatMessages = mutableListOf<ChatMessages>()
    private lateinit var chatAdapter : AI_adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val sharetheme = requireActivity().getSharedPreferences("theme", MODE_PRIVATE)
        val savedTheme =
            sharetheme.getString("themeOption", ThemeHelper.SYSTEM) ?: ThemeHelper.SYSTEM
        ThemeHelper.applyTheme(savedTheme)

        _binding = FragmentAiSuggestionsBinding.inflate(inflater,container,false)

        val viewModelAI = ViewModelProvider(this)[AI_Suggestion_ViewModel::class.java]

        val rv = bindning.RvAISuggestion

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        rv.layoutManager = linearLayoutManager

        chatAdapter = AI_adapter(chatMessages)

        rv.adapter = chatAdapter


        viewModelAI.chatHistory.observe(viewLifecycleOwner){chat->
            chatMessages.clear()
            chatMessages.addAll(chat.reversed())

            if(!chat.isEmpty()){
                bindning.TvHowAssistAI.visibility = View.GONE
            }else{
                bindning.TvHowAssistAI.visibility = View.VISIBLE
            }

            chatAdapter.notifyDataSetChanged()
        }

        val etQuery = bindning.EtSearchAIFragment

        viewModelAI.waitForResponse.observe(viewLifecycleOwner){bool->
            if (bool){
                etQuery.isEnabled = false
            }else{
                etQuery.isEnabled = true
            }
        }

        bindning.IbSendBtnAI.setOnClickListener {
            val query = etQuery.text.toString()
            if (query.isEmpty()){
                Toast.makeText(requireContext(),"Message is Empty",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModelAI.sendMessage(query)
            }

            etQuery.text.clear()
        }

        return bindning.root
    }

}