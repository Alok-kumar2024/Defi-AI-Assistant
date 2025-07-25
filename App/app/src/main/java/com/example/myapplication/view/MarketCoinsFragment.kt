package com.example.myapplication.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMarketBinding
import com.example.myapplication.databinding.FragmentMarketCoinsBinding
import com.example.myapplication.model.Article
import com.example.myapplication.model.Coin
import com.example.myapplication.model.ThemeHelper
import com.example.myapplication.model.searchMarket
import com.example.myapplication.viewModel.MarketNewsAdapter
import com.example.myapplication.viewModel.MarketViewModel
import com.example.myapplication.viewModel.marketCoinsAdapter


class MarketCoinsFragment : Fragment(),searchMarket {

    private var _binding : FragmentMarketCoinsBinding? = null
    private val binding get() = _binding!!

    private lateinit var coinsRv : RecyclerView
    private lateinit var coinsAdapter : marketCoinsAdapter
    private val coinsDataList = mutableListOf<Coin>()

    private val coinAll = mutableListOf<Coin>()
    private val topGainerCoins = mutableListOf<Coin>()
    private val topLoserCoins = mutableListOf<Coin>()

    private var refreshing = false
    private var pendingCalls = 0

    private var currentChip = 1

    private lateinit var filterCoinsAll : List<Coin>
    private lateinit var filterTopGainer :List<Coin>
    private lateinit var filterTopLoser :List<Coin>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val sharetheme = requireActivity().getSharedPreferences("theme", MODE_PRIVATE)
        val savedTheme =
            sharetheme.getString("themeOption", ThemeHelper.SYSTEM) ?: ThemeHelper.SYSTEM
        ThemeHelper.applyTheme(savedTheme)

        _binding = FragmentMarketCoinsBinding.inflate(inflater,container,false)

        val marketViewModel = ViewModelProvider(this)[MarketViewModel::class.java]

        val swipLayout = binding.SwipRefreshLayoutMarket


        coinsRv = binding.RvCryptoCoins
        coinsRv.layoutManager = LinearLayoutManager(requireContext())

        coinsAdapter = marketCoinsAdapter(coinsDataList)

        coinsRv.adapter = coinsAdapter


        marketViewModel.coinsData.observe(viewLifecycleOwner){coins->
            coinsDataList.clear()
            coinsDataList.addAll(coins)

            coinAll.clear()
            coinAll.addAll(coins)

            coinsAdapter.notifyDataSetChanged()

            onOneCall()
        }

        marketViewModel.isLoading.observe(viewLifecycleOwner){loading->
            if (loading){
                binding.LoadingTokenMarketCoinsFragment.repeatCount = LottieDrawable.INFINITE
                binding.LoadingTokenMarketCoinsFragment.visibility = View.VISIBLE
                binding.LoadingTokenMarketCoinsFragment.playAnimation()

                binding.RvCryptoCoins.visibility = View.GONE
            }else{
                binding.LoadingTokenMarketCoinsFragment.visibility = View.GONE
                binding.LoadingTokenMarketCoinsFragment.cancelAnimation()

                binding.RvCryptoCoins.visibility = View.VISIBLE

                if (coinsDataList.isEmpty()){
                    binding.TvNoCoinsFoundMarketFragment.visibility = View.VISIBLE
                    binding.RvCryptoCoins.visibility = View.GONE
                }else{
                    binding.TvNoCoinsFoundMarketFragment.visibility = View.GONE
                    binding.RvCryptoCoins.visibility = View.VISIBLE
                }
            }
        }


        marketViewModel.coinsDataGainer.observe(viewLifecycleOwner){gain->
            topGainerCoins.clear()
            topGainerCoins.addAll(gain)
        }

        marketViewModel.coinsDataLoser.observe(viewLifecycleOwner){lose->
            topLoserCoins.clear()
            topLoserCoins.addAll(lose)
        }

        binding.ChipGroupMarketFragment.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.ChipAll -> {
                    coinsDataList.clear()
                    coinsDataList.addAll(coinAll)

                    coinsAdapter.notifyDataSetChanged()
                    currentChip = 1
                }
                R.id.ChipTopGainer->{
                    coinsDataList.clear()
                    coinsDataList.addAll(topGainerCoins)

                    coinsAdapter.notifyDataSetChanged()
                    currentChip = 2
                }
                R.id.ChipTopLoser->{
                    coinsDataList.clear()
                    coinsDataList.addAll(topLoserCoins)

                    coinsAdapter.notifyDataSetChanged()

                    currentChip = 3
                }
            }
        }

        swipLayout.setOnRefreshListener {

            refreshing = true
            pendingCalls = 1
            swipLayout.isRefreshing = true

            binding.ChipGroupMarketFragment.check(R.id.ChipAll)

            marketViewModel.fetchCryptoCoin()
        }

        marketViewModel.fetchCryptoCoin()

        return binding.root
    }

    private fun onOneCall(){
        if (refreshing){
            pendingCalls--
            if(pendingCalls<=0){
                refreshing = false
                binding.SwipRefreshLayoutMarket.isRefreshing = false

                coinsDataList.clear()
                coinsDataList.addAll(coinAll)
                coinsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun search(query: String) {


        when(currentChip){
            1 ->{
                filterCoinsAll = if (query.isBlank()) coinAll else {
                    coinAll.filter {
                        it.name.contains(query, ignoreCase = true) || it.symbol.contains(query,true)
                    }
                }
                coinsDataList.clear()
                coinsDataList.addAll(filterCoinsAll)
                coinsAdapter.notifyDataSetChanged()

                binding.TvNoCoinsFoundMarketFragment.visibility = if (filterCoinsAll.isEmpty()) View.VISIBLE else View.GONE
            }
            2->{
                filterTopGainer = if (query.isBlank()) topGainerCoins else {
                    topGainerCoins.filter {
                        it.name.contains(query, ignoreCase = true) || it.symbol.contains(query,true)
                    }
                }
                coinsDataList.clear()
                coinsDataList.addAll(filterTopGainer)
                coinsAdapter.notifyDataSetChanged()

                binding.TvNoCoinsFoundMarketFragment.visibility = if (filterTopGainer.isEmpty()) View.VISIBLE else View.GONE
            }
            3->{
                filterTopLoser = if (query.isBlank()) topLoserCoins else {
                    topLoserCoins.filter {
                        it.name.contains(query, ignoreCase = true) || it.symbol.contains(query,true)
                    }
                }
                coinsDataList.clear()
                coinsDataList.addAll(filterTopLoser)
                coinsAdapter.notifyDataSetChanged()

                binding.TvNoCoinsFoundMarketFragment.visibility = if (filterTopLoser.isEmpty()) View.VISIBLE else View.GONE
            }
        }



    }

}