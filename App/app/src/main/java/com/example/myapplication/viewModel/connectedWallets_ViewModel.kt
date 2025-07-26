package com.example.myapplication.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class connectedWallets_ViewModel() : ViewModel() {

    private val _connectData = MutableLiveData<MutableList<ConnectedwalletData>>()
    val connectData : LiveData<MutableList<ConnectedwalletData>> = _connectData

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading : LiveData<Boolean> = _isLoading

    fun loadingData(userID : String , address : String, context : Context){
        _isLoading.value = true

        val database = FirebaseDatabase.getInstance().getReference("USERS")

        database.child(userID).child("wallets").addValueEventListener( object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<ConnectedwalletData>()
                if (snapshot.exists()){
                    for (walletSnapshot in snapshot.children){
                        val name = (walletSnapshot.child("walletName").value ?: "No Name").toString()
                        val address = (walletSnapshot.key).toString()
                        val choosen = walletSnapshot.child("choosen").value as Boolean
                        val imgUrl = (walletSnapshot.child("walletImg").value).toString()

                        val data = ConnectedwalletData(imgUrl,name,address,choosen)
                        dataList.add(data)
                    }

                    _connectData.value = dataList

                }else{
                    Toast.makeText(context,"No Wallet Connected",Toast.LENGTH_SHORT).show()
                }
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Couldn't Fetched Wallets Data", Toast.LENGTH_SHORT).show()
                Log.e("ConnectedWallet","The Error is : ${error.message}")

                _isLoading.value = false
            }

        })

    }


}