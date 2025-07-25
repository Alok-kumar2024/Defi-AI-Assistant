package com.example.myapplication.view

import android.os.Bundle
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
import com.example.myapplication.databinding.FragmentMarketCoinsBinding
import com.example.myapplication.databinding.FragmentMarketNewsBinding
import com.example.myapplication.model.Article
import com.example.myapplication.model.Coin
import com.example.myapplication.model.ThemeHelper
import com.example.myapplication.model.searchMarket
import com.example.myapplication.viewModel.MarketNewsAdapter
import com.example.myapplication.viewModel.MarketViewModel
import com.example.myapplication.viewModel.marketCoinsAdapter


class MarketNewsFragment : Fragment(),searchMarket {

    private var _binding : FragmentMarketNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsRv : RecyclerView
    private lateinit var newsAdapter: MarketNewsAdapter
    private val newsDataList = mutableListOf<Article>()
    private val newsDataListAll = mutableListOf<Article>()

    private var refreshing = false
    private var pendingCalls = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharetheme = requireActivity().getSharedPreferences("theme", MODE_PRIVATE)
        val savedTheme =
            sharetheme.getString("themeOption", ThemeHelper.SYSTEM) ?: ThemeHelper.SYSTEM
        ThemeHelper.applyTheme(savedTheme)

        _binding = FragmentMarketNewsBinding.inflate(inflater,container,false)

        val marketViewModel = ViewModelProvider(this)[MarketViewModel::class.java]

        val swipLayout = binding.SwipRefreshLayoutMarket


        newsRv = binding.RvCryptoNews
        newsRv.layoutManager = LinearLayoutManager(requireContext())

        newsAdapter = MarketNewsAdapter(newsDataList)

        newsRv.adapter = newsAdapter

        marketViewModel.isLoading.observe(viewLifecycleOwner){loading->
            if (loading){
                binding.LoadingTokenMarketNewsFragment.repeatCount = LottieDrawable.INFINITE
                binding.LoadingTokenMarketNewsFragment.visibility = View.VISIBLE
                binding.LoadingTokenMarketNewsFragment.playAnimation()

                binding.RvCryptoNews.visibility = View.GONE
            }else{
                binding.LoadingTokenMarketNewsFragment.visibility = View.GONE
                binding.LoadingTokenMarketNewsFragment.cancelAnimation()

                binding.RvCryptoNews.visibility = View.VISIBLE

                if (newsDataList.isEmpty()){
                    binding.TvNoNewsFoundMarketFragment.visibility = View.VISIBLE
                    binding.RvCryptoNews.visibility = View.GONE
                }else{
                    binding.TvNoNewsFoundMarketFragment.visibility = View.GONE
                    binding.RvCryptoNews.visibility = View.VISIBLE
                }
            }
        }

        marketViewModel.newsData.observe(viewLifecycleOwner){news->

            newsDataList.clear()
            newsDataList.addAll(news)

            newsDataListAll.clear()
            newsDataListAll.addAll(news)

            newsAdapter.notifyDataSetChanged()
            onOneCall()

        }


        swipLayout.setOnRefreshListener {

            refreshing = true
            pendingCalls = 1
            swipLayout.isRefreshing = true

            marketViewModel.fetchCryptoNews()
        }

        marketViewModel.fetchCryptoNews()

        return binding.root
    }

    private fun onOneCall(){
        if (refreshing){
            pendingCalls--
            if(pendingCalls<=0){
                refreshing = false
                binding.SwipRefreshLayoutMarket.isRefreshing = false

            }
        }
    }

    override fun search(query: String) {

        val filterNews = if (query.isBlank()) newsDataListAll else{
            newsDataListAll.filter {
                it.title.contains(query,true) || it.description?.contains(query,true) == true
            }
        }

        newsDataList.clear()
        newsDataList.addAll(filterNews)

        newsAdapter.notifyDataSetChanged()

        binding.TvNoNewsFoundMarketFragment.visibility = if (filterNews.isEmpty()) View.VISIBLE else View.GONE

    }

}