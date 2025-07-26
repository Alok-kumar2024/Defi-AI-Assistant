package com.example.myapplication.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityWalletSettingsBinding
import com.example.myapplication.viewModel.ConnectedWalletAdapter
import com.example.myapplication.viewModel.ConnectedwalletData
import com.example.myapplication.viewModel.connectedWallets_ViewModel

class WalletSettingsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityWalletSettingsBinding

    private lateinit var walletRv : RecyclerView
    private lateinit var walletAdapter : ConnectedWalletAdapter
    private var walletData = mutableListOf<ConnectedwalletData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWalletSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val share = getSharedPreferences("SIGNUP", Context.MODE_PRIVATE)
        val userID = share.getString("UNIQUEKEY", null) ?: "Not Got"
       val  addressFromSignIn = share.getString("address", null) ?: "Not Got"
        val choosenFromSignIn = share.getBoolean("choosen", false)

        Log.d("ConnectedWallet Activity","The Value of UserId -> $userID\n" +
                "The Value of Addres : $addressFromSignIn\n" +
                "The Value of Choosen : $choosenFromSignIn")


        binding.IbBackButtonOfConnectedWallet.setOnClickListener {
            finish()
        }

        val viewModelWallet = ViewModelProvider(this)[connectedWallets_ViewModel::class.java]

        walletRv = binding.RvWallets
        walletRv.layoutManager = LinearLayoutManager(this)

        walletAdapter = ConnectedWalletAdapter(walletData, onClick = {data->
            Toast.makeText(this,"Clicked on Wallet with address ${data.walletAddress}.",Toast.LENGTH_SHORT).show()
        })

        walletRv.adapter = walletAdapter

        viewModelWallet.connectData.observe(this){data->

            walletData.clear()
            walletData.addAll(data)

            walletAdapter.notifyDataSetChanged()
        }

        viewModelWallet.isLoading.observe(this){loading->
            if (loading){
                binding.LoadingLottiAnimationConnectedWallet.repeatCount = LottieDrawable.INFINITE
                binding.LoadingLottiAnimationConnectedWallet.playAnimation()
                binding.LoadingLottiAnimationConnectedWallet.visibility = View.VISIBLE
            }else{
                binding.LoadingLottiAnimationConnectedWallet.cancelAnimation()
                binding.LoadingLottiAnimationConnectedWallet.visibility = View.GONE
            }
        }

        viewModelWallet.loadingData(userID,addressFromSignIn,this)


    }
}