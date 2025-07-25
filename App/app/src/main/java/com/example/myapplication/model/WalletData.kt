package com.example.myapplication.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class WalletData(
    val address: String = "",
    val chain: String = "",
    val fetchedAt: String = "",
    val nativeBalance: NativeBalance? = null,
    val tokenBalances: List<TokenBalance> = emptyList(),
    val nftBalances: List<NftBalance> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val netWorth: NetWorth? = null,
    val analytics: Analytics? = null,
    val errors: List<ErrorEntry> = emptyList(),

    val userId: String = "", // 🔹 NEW
    val lastUpdated: Timestamp? = null, // 🔹 NEW
    val createdAt: Timestamp? = null // 🔹 NEW
)
@IgnoreExtraProperties
data class NativeBalance(
    val balance: String = "",
    val symbol: String = "",
    val decimals: Int = 18
)

@IgnoreExtraProperties
data class TokenBalance(
    val tokenAddress: String = "",
    val name: String = "",
    val symbol: String = "",
    val balance: String = "",
    val decimals: Int? = 18,
    val readableBalance: Double = 0.0,
    val valueUsd: Double = 0.0,
    val priceUsd: Double = 0.0,
    val changePercent24h: Double = 0.0,
    val logo: String? = null
)

@IgnoreExtraProperties
data class NftBalance(
    val tokenAddress: String = "",
    val tokenId: String = "",
    val name: String? = null,
    val symbol: String? = null,
    val image: String? = null,
    val metadata: String? = null
)

@IgnoreExtraProperties
data class Transaction(
    val hash: String = "",
    val type: String = "",          // "send" or "receive"
    val assetType: String = "",     // "native", "ERC20", "NFT"
    val symbol: String = "",
    val name: String = "",
    val logo: String? = null,
    val amount: Double = 0.0,
    val amountUsd: Double = 0.0,
    val fromAddress: String = "",
    val toAddress: String = "",
    val timestamp: String = ""
)

@IgnoreExtraProperties
data class NetWorth(
    val totalNetworthUsd: Double = 0.0
)

@IgnoreExtraProperties
data class Analytics(
    val totalTokenValueUsd: Double = 0.0,
    val topToken: TokenPie = TokenPie(),
    val tokenDistribution: List<TokenPie> = emptyList()
)

@IgnoreExtraProperties
data class TokenPie(
    val name: String = "",
    val valueUsd: Double = 0.0,
    val sharePercent: String = "0.0"
)

@IgnoreExtraProperties
data class ErrorEntry(
    val type: String = "",
    val error: String = ""
)


