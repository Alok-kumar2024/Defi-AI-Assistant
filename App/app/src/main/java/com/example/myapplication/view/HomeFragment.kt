package com.example.myapplication.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.firebase.database.FirebaseDatabase
import com.walletconnect.android.CoreClient
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.addHeaderLenient
import org.json.JSONObject
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var selectedWalletNative: String? = null
    private var selectedWalletUniversal: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val BtnWalletConnect = binding.BtnWalletConnect

        val share = requireContext().getSharedPreferences("SIGNUP", Context.MODE_PRIVATE)
        val addressFromSignIn = share.getString("address",null) ?: "Not Got"
        val choosenFromSignIn = share.getBoolean("choosen",false)

        if (choosenFromSignIn)
        {
            binding.LLWalletConnectHomeFragment.visibility = View.GONE
            binding.SvWholeWalletUI.visibility = View.VISIBLE
        }else{
            binding.LLWalletConnectHomeFragment.visibility = View.VISIBLE
            binding.SvWholeWalletUI.visibility = View.GONE
        }


        BtnWalletConnect.setOnClickListener {

            lifecycleScope.launch {
                showWalletList()
            }

        }

        setupSignDelegate()


        return binding.root
    }

    private suspend fun showWalletList() {
        val wallets = fetchWallets()
        val names = wallets.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Select Wallet")
            .setItems(names) { _, idx ->
                val wallet = wallets[idx]
                val uri = wallet.native?.takeIf { it.isNotBlank() }
                    ?: wallet.universal?.takeIf { it.isNotBlank() }

                if (uri != null) {
                    selectedWalletNative = wallet.native
                    selectedWalletUniversal = wallet.universal
                    initiaieWalletConnect()
                } else {
                    Log.e("WalletConnect", "Wallet ${wallet.name} has no valid URI\n" +
                            "The Native Link ${wallet.native}\n" +
                            "The Universal Link ${wallet.universal}.")
                    Toast.makeText(
                        requireContext(),
                        "No connection URI available",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }.setNegativeButton("Cancel", null)
            .show()

    }

    private fun initiaieWalletConnect() {
        val pairing = CoreClient.Pairing.create { error ->
            Log.e("WalletConnect", "Pairing creation failed: ${error.throwable}")
        } ?: return

        val wcUri = pairing.uri
        val encodedWcUri = Uri.encode(wcUri)

        val connectParams = Sign.Params.Connect(
            pairing = pairing,
            namespaces = mapOf(
                "eip155" to Sign.Model.Namespace.Proposal(
                    chains = listOf("eip155:1"),
                    methods = listOf("eth_sendTransaction", "personal_sign", "eth_signTypedData"),
                    events = listOf("accountsChanged", "chainChanged")
                )
            )
        )

        SignClient.connect(
            connectParams,
            onSuccess = {
                Log.d("WalletConnect", "Connection initiated")

//                val fullUri = "${selectedWalletNative ?: selectedWalletUniversal}?wc=${Uri.encode(wcUri)}"
//                connectViaURI(fullUri)
//                val nativeUri = selectedWalletNative?.let { "$it?uri=$encodedWcUri" }
//                val universalUri = selectedWalletUniversal?.let { "$it/wc?uri=$encodedWcUri" }

                val nativeUri = selectedWalletNative?.let {
                    val clean = it.removeSuffix("/")
                    if (clean.contains("://")) {
                        // Check if it ends with "/wc" or similar mistake
                        val uriBase = if (clean.endsWith("/wc")) clean.removeSuffix("/wc") else clean
                        "$uriBase?uri=$encodedWcUri"
                    } else {
                        "$clean/wc?uri=$encodedWcUri"
                    }
                }


                val universalUri = selectedWalletUniversal?.let {
                    val clean = it.removeSuffix("/")  // strip trailing slash
                    when {
                        clean.endsWith("/wc") -> "$clean?uri=$encodedWcUri"
                        else -> "$clean/wc?uri=$encodedWcUri"
                    }
                }


                Log.d("WalletConnect", "Trying to open:\nNative: $nativeUri\nUniversal: $universalUri")

                connectViaURI(nativeUri, universalUri)
            },
            onError = {
                Log.e("WalletConnect", "Connection error: ${it.throwable}")

            }
        )

    }

    private suspend fun fetchWallets(): List<WalletItem> = withContext(Dispatchers.IO) {

            val client = OkHttpClient()
        val reuqest = Request.Builder()
            .url("https://explorer-api.walletconnect.com/v3/wallets?projectId=141281e6ec8c25ea34e7dd4497ab5d58")
            .header("User-Agent", "WalletConnectKotlinApp/1.0")
            .build()

        val response = client.newCall(reuqest).execute()
        val result = response.body?.string() ?: return@withContext emptyList()

        val wallets = mutableListOf<WalletItem>()
        val json = JSONObject(result)
        val listings = json.getJSONObject("listings")
        val keys = listings.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val walletJson = listings.getJSONObject(key)

            val mobile = walletJson.getJSONObject("mobile")
            val native = mobile.optString("native",null)
            val universal = mobile.optString("universal",null)


            if (!native.isNullOrBlank() || !universal.isNullOrBlank())
            {

                wallets.add(
                    WalletItem(
                        name = walletJson.getString("name"),
                        image = walletJson.getString("image_id"),
                        native = native,
                        universal = universal
                    )
                )
            }else {
                Log.w("WalletConnect", "Skipping wallet: ${walletJson.getString("name")} — no valid URI")
            }
//            wallets.add(
//                WalletItem(
//                    name = walletJson.getString("name"),
//                    image = walletJson.getString("image_id"),
//                    native = walletJson.optString("native", null),
//                    universal = walletJson.optString("universal", null)
//                )
//            )
        }
        wallets.sortedBy { it.name.lowercase() }
    }

//    private fun connectViaURI(uri: String?) {
//
//        try {
//            val parsedUri = Uri.parse(uri)
//            val nativeIntent = Intent(Intent.ACTION_VIEW, parsedUri)
//
//            val canOpenNative = nativeIntent.resolveActivity(requireContext().packageManager) != null
//
//            Log.d("walletConnet","The CanOpenNative is : $canOpenNative")
//
//            if (canOpenNative) {
//                startActivity(nativeIntent)
//            } else {
//                val fallbackUniversal = selectedWalletUniversal
//                val wcQuery = parsedUri.getQueryParameter("wc")
//
//                if (!fallbackUniversal.isNullOrBlank() && !wcQuery.isNullOrBlank()) {
//                    val fallbackUri = "$fallbackUniversal?wc=${Uri.encode(wcQuery)}"
//                    Log.d("WalletConnect", "Fallback to browser: $fallbackUri")
//                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUri))
//
////                    if (browserIntent.resolveActivity(requireContext().packageManager) != null) {
//                        startActivity(browserIntent)
////                    } else {
////                        Toast.makeText(requireContext(), "No browser found to open wallet", Toast.LENGTH_SHORT).show()
////                    }
//                } else {
//                    requireActivity().runOnUiThread {
//                        Toast.makeText(requireContext(), "No valid URI to open", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("WalletConnect", "Exception while opening URI: $e")
//            Toast.makeText(requireContext(), "Invalid wallet link", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun connectViaURI(nativeUri: String?, universalUri: String?) {
//        try {
//            var launched = false
//
//            if (!nativeUri.isNullOrBlank()) {
//                val nativeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(nativeUri))
//                if (nativeIntent.resolveActivity(requireContext().packageManager) != null) {
//                    Log.d("WalletConnect", "Launching native URI: $nativeUri")
//                    startActivity(nativeIntent)
//                    launched = true
//                }
//            }
//
//            if (!launched && !universalUri.isNullOrBlank()) {
//                val universalIntent = Intent(Intent.ACTION_VIEW, Uri.parse(universalUri))
//                if (universalIntent.resolveActivity(requireContext().packageManager) != null) {
//                    Log.d("WalletConnect", "Launching universal URI: $universalUri")
//                    startActivity(universalIntent)
//                    launched = true
//                }
//            }
//
//            if (!launched) {
//                Log.w("WalletConnect", "No wallet app could handle the intent")
//                requireActivity().runOnUiThread {
//                    Toast.makeText(requireContext(), "No wallet app found", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//
//        } catch (e: Exception) {
//            Log.e("WalletConnect", "URI open failed", e)
//            requireActivity().runOnUiThread {
//                Toast.makeText(requireContext(), "Failed to open wallet", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun connectViaURI(nativeUri: String?, universalUri: String?) {
        try {
            var launched = false

            nativeUri?.let {
                try {
                    Log.d("WalletConnect", "Attempting native URI: $it")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    startActivity(intent)
                    launched = true
                } catch (e: Exception) {
                    Log.w("WalletConnect", "Native intent failed: ${e.message}")
                }
            }

            if (!launched && !universalUri.isNullOrBlank()) {
                try {
                    Log.d("WalletConnect", "Attempting universal URI: $universalUri")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(universalUri))
                    startActivity(intent)
                    launched = true
                } catch (e: Exception) {
                    Log.w("WalletConnect", "Universal intent failed: ${e.message}")
                }
            }

            if (!launched) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "No wallet app found", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        } catch (e: Exception) {
            Log.e("WalletConnect", "URI open failed", e)
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Failed to open wallet", Toast.LENGTH_SHORT).show()
            }
        }
    }





    private fun setupSignDelegate() {
        SignClient.setDappDelegate(object : SignClient.DappDelegate {

            // 🔌 Connection state changes (e.g., relay online/offline)
            override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
                Log.d("WalletConnection", "Connection state: ${state.isAvailable}")
                if (!state.isAvailable) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Disconnected from WalletConnect relay",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            // ❌ Internal SDK errors
            override fun onError(error: Sign.Model.Error) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "WalletConnect error occurred",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                Log.e("WalletConnection", "Internal Error Due to SDK : Error ${error.throwable}")
            }

            // ✅ Session approved — wallet connected
            override fun onSessionApproved(approvedSession: Sign.Model.ApprovedSession) {

                val walletAddress = approvedSession.accounts.firstOrNull()?.split(":")?.last()
                Log.d("WalletConnection", "Approved wallet Address : $walletAddress")

                requireActivity().runOnUiThread {
                    binding.LLWalletConnectHomeFragment.visibility = View.GONE
                    binding.SvWholeWalletUI.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Successfully Connected To Wallet",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (!walletAddress.isNullOrEmpty()) {
                    updateDatabase(address = walletAddress)
                }

                // TODO: Save walletAddress to ViewModel or SharedPreferences if needed

            }

            // 🔌 Session deleted (e.g., user disconnected from wallet)
            override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
                Log.d("WalletConnect", "Session deleted")

                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Wallet disconnected", Toast.LENGTH_SHORT).show()
                    binding.LLWalletConnectHomeFragment.visibility = View.VISIBLE
                    binding.SvWholeWalletUI.visibility = View.GONE
                }
                // TODO: Clear wallet address from memory or UI
            }

            // 📡 Wallet emitted an event (e.g., chainChanged)
            override fun onSessionEvent(sessionEvent: Sign.Model.SessionEvent) {
                Log.d("WalletConnect", "Session event: ${sessionEvent.name}")

                // TODO: React to events like chain/network changes

            }

            // ⏳ Session extended (e.g., expiration time updated)
            override fun onSessionExtend(session: Sign.Model.Session) {
                Log.d("WalletConnect", "Session extended: ${session.topic}")

                // Optional: Show new expiration time or keep-alive

            }

            // 🚫 User rejected the connection request
            override fun onSessionRejected(rejectedSession: Sign.Model.RejectedSession) {
                Log.e("WalletConnection", "Session rejected")
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Wallet connection rejected",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }

            // 📩 Response to a request (e.g., signing, transaction)
            override fun onSessionRequestResponse(response: Sign.Model.SessionRequestResponse) {
                Log.d("WalletConnect", "Session response: $response")
                // TODO: Handle transaction result or signature here

            }

            // 🔄 Session updated (e.g., new accounts or chain)
            override fun onSessionUpdate(updatedSession: Sign.Model.UpdatedSession) {
                Log.d("WalletConnect", "Session updated: $updatedSession")

                // TODO: Update wallet info if needed

            }
        })
    }

    private fun updateDatabase(address: String) {
        val share = requireContext().getSharedPreferences("SIGNUP", Context.MODE_PRIVATE)
        val unqiueKey = share.getString("UNIQUEKEY", null)

        Log.d("WalletConnect", "Trying to save address: $address for key: $unqiueKey")

        if (!unqiueKey.isNullOrEmpty()) {
            val addressMap = mapOf(
                "address" to address,
                "choosen" to true
                )
            FirebaseDatabase.getInstance().getReference("USERS").child(unqiueKey).child("wallets")
                .child(address).updateChildren(addressMap).addOnCompleteListener { task->
                    if(task.isSuccessful)
                    {
                        Log.d("WalletConnect","SuccessFully Stored Address in RealTimeDatabase")
                    }else{
                        Log.d("WalletConnect","Error Storing Address in RealTimeDatabase Error:${task.exception}")
                    }
                }
        }else {
            Log.e("WalletConnect", "UNIQUEKEY is null or empty")
        }
    }

    private fun disconnectAllSessions() {
        val sessions = SignClient.getListOfSettledSessions()

        for (session in sessions) {
            SignClient.disconnect(
                disconnect = Sign.Params.Disconnect(
                    sessionTopic = session.topic
                ),
                onSuccess = {
                    Log.d("WalletConnect", "Successfully disconnected session: ${session.topic}")
                },
                onError = {error->
                    Log.e("WalletConnect", "Failed to disconnect session: ${session.topic}, error: ${error.throwable}")
                }

            )
        }
    }




    data class WalletItem(
        val name: String,
        val image: String,
        val native: String?,
        val universal: String?
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}