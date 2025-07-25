package com.example.myapplication.view

import android.content.Context
import android.graphics.Color
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
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.airbnb.lottie.LottieDrawable
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAnalyticsBinding
import com.example.myapplication.model.Analytics
import com.example.myapplication.model.ThemeHelper
import com.example.myapplication.model.TokenPie
import com.example.myapplication.viewModel.AnalyticsViewModel
import com.example.myapplication.viewModel.TokenListAnalyticsAdapterRv
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate


class AnalyticsFragment : Fragment() {
    private var _binding : FragmentAnalyticsBinding ?= null
    private val binding get() = _binding!!

    private lateinit var tokenListRv : RecyclerView
    private var tokenListData = mutableListOf<TokenPie>()
    private lateinit var tokenListAdapter : TokenListAnalyticsAdapterRv

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val sharetheme = requireActivity().getSharedPreferences("theme", MODE_PRIVATE)
        val savedTheme =
            sharetheme.getString("themeOption", ThemeHelper.SYSTEM) ?: ThemeHelper.SYSTEM
        ThemeHelper.applyTheme(savedTheme)

        _binding = FragmentAnalyticsBinding.inflate(inflater,container,false)

        val share = requireContext().getSharedPreferences("SIGNUP", Context.MODE_PRIVATE)
        val userID = share.getString("UNIQUEKEY", null) ?: "Not Got"
        val addressFromSignIn = share.getString("address", null) ?: "Not Got"
        val choosenFromSignIn = share.getBoolean("choosen", false)

        val analyticsViewModel = ViewModelProvider(this)[AnalyticsViewModel ::class.java]

        if (choosenFromSignIn){

            binding.WholeAnalytics.visibility = View.VISIBLE

            tokenListRv = binding.rvTokenDistributionAnalytics
            tokenListRv.layoutManager = LinearLayoutManager(requireContext())

            tokenListAdapter = TokenListAnalyticsAdapterRv(tokenListData)

            tokenListRv.adapter = tokenListAdapter

            analyticsViewModel.isLoading.observe(viewLifecycleOwner){loading->
                if (loading){
                    binding.rvTokenDistributionAnalytics.visibility = View.GONE
                    binding.LottieAnimationLoadingAnalytics.repeatCount = LottieDrawable.INFINITE
                    binding.LottieAnimationLoadingAnalytics.playAnimation()
                    binding.LottieAnimationLoadingAnalytics.visibility = View.VISIBLE
                }else{
                    binding.rvTokenDistributionAnalytics.visibility = View.VISIBLE
                    binding.LottieAnimationLoadingAnalytics.visibility = View.GONE
                    binding.LottieAnimationLoadingAnalytics.cancelAnimation()

//                    if (tokenListData.isEmpty()){
//                        binding.TvNoTokensFoundAnalytics.visibility = View.VISIBLE
//                    }else{
//                        binding.TvNoTokensFoundAnalytics.visibility = View.GONE
//                    }
                }
            }

            analyticsViewModel.tokenListData.observe(viewLifecycleOwner){tokenList->
                tokenListData.clear()
                tokenListData.addAll(tokenList)

                tokenListAdapter.notifyDataSetChanged()

                if (tokenListData.isEmpty()){
                    binding.TvNoTokensFoundAnalytics.visibility = View.VISIBLE
                }else{
                    binding.TvNoTokensFoundAnalytics.visibility = View.GONE
                }
            }

            analyticsViewModel.analytics.observe(viewLifecycleOwner){analytics->

                Log.d("AnalyticsFragment", "Received analytics: $analytics")

                binding.tvTopTokenNameAnalytics.text = analytics?.topToken?.name

                binding.tvTopTokenValueAnalytics.text = "$${analytics?.topToken?.valueUsd} (${analytics?.topToken?.sharePercent}%)"

                binding.tvTotalValueAnalytics.text = "$${String.format("%.2f",analytics?.totalTokenValueUsd)}"

                if (analytics != null) {
                    updatePieChart(analytics)
                }

            }


            analyticsViewModel.AnalyticsData(userID,addressFromSignIn)

        }else{
            binding.WholeAnalytics.visibility = View.GONE
        }



        return binding.root
    }

    private fun updatePieChart(analytics : Analytics){
        val pieChart = binding.pieChartAnalytics

        binding.tvTotalValueAnalytics.text = "$${String.format("%.2f", analytics.totalTokenValueUsd)}"

        val entries = ArrayList<PieEntry>()

        analytics.tokenDistribution.forEach{
            entries.add(PieEntry(it.valueUsd.toFloat(),it.name))
        }

        //DataSet
        val dataSet = PieDataSet(entries,"")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE

        //set data
        val pieData = PieData(dataSet)
        pieChart.data = pieData

        pieChart.setDrawEntryLabels(false)

        pieChart.legend.isEnabled = true
        pieChart.legend.textColor = Color.WHITE
        pieChart.legend.textSize = 12f
        pieChart.legend.isWordWrapEnabled = true

        // Refresh Chart
        pieChart.notifyDataSetChanged()
        pieChart.invalidate()

        //Chart Style
        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.centerText = "Portfolio"
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.animateY(1000)

    }

}