package com.example.myapplication.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMarketBinding
import com.example.myapplication.model.Article
import com.example.myapplication.model.Coin
import com.example.myapplication.model.NewsResponse
import com.example.myapplication.model.ThemeHelper
import com.example.myapplication.model.searchMarket
import com.example.myapplication.viewModel.BottomNav_ViewModel
import com.example.myapplication.viewModel.MarketNewsAdapter
import com.example.myapplication.viewModel.MarketViewModel
import com.example.myapplication.viewModel.MarkteBottom
import com.example.myapplication.viewModel.marketCoinsAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MarketFragment : Fragment() {
    private var _binding : FragmentMarketBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentFragment : MarkteBottom

    private var coinsFragment : MarketCoinsFragment? = null
    private var newsFragment : MarketNewsFragment? = null

    private var searchJob : Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val sharetheme = requireActivity().getSharedPreferences("theme", MODE_PRIVATE)
        val savedTheme =
            sharetheme.getString("themeOption", ThemeHelper.SYSTEM) ?: ThemeHelper.SYSTEM
        ThemeHelper.applyTheme(savedTheme)

        _binding = FragmentMarketBinding.inflate(inflater,container,false)

        val bottomViewModel = ViewModelProvider(this)[BottomNav_ViewModel::class.java]

        if (coinsFragment === null){
            coinsFragment = MarketCoinsFragment()
            childFragmentManager.beginTransaction()
                .add(R.id.FrameLayoutMarket,coinsFragment!!,"coinsFragment")
                .commit()
        }

        if (newsFragment == null){
            newsFragment = MarketNewsFragment()
            childFragmentManager.beginTransaction()
                .add(R.id.FrameLayoutMarket,newsFragment!!,"newsFragment")
                .commit()
        }

        childFragmentManager.executePendingTransactions()

        currentFragment = bottomViewModel.marketBottomNav.value ?: MarkteBottom.COIN
        showFragment(currentFragment)

        val bottomnav = binding.bottomNavMarketFragment

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (currentFragment != MarkteBottom.COIN){
                    bottomViewModel.marketBottomChoosen(MarkteBottom.COIN)
                    bottomnav.selectedItemId = R.id.MarketNav_Coins
                }else{
                    requireActivity().finish()
                }
            }

        })

        bottomViewModel.marketBottomNav.observe(viewLifecycleOwner){nav->
            if (nav != currentFragment){
                currentFragment = nav
                showFragment(nav)
            }
        }


        bottomnav.setOnItemSelectedListener {

            binding.EtSearchMarketFragmentMarket.text.clear()

            when(it.itemId){
                R.id.MarketNav_Coins -> {
                    bottomViewModel.marketBottomChoosen(MarkteBottom.COIN)
                    true
                }
                R.id.MarketNav_News ->{
                    bottomViewModel.marketBottomChoosen(MarkteBottom.NEWS)
                    true
                }
                else -> {false}
            }
        }

        bottomnav.setOnItemReselectedListener {  }

        //This thing is creating problem
        binding.EtSearchMarketFragmentMarket.doOnTextChanged { text, start, before, count ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(300)
                val q = text.toString().orEmpty()

                val target = getSearchTarget()
                Log.d("SearchCheck", "Dispatching to: ${target?.javaClass?.simpleName}  query='$q'")
                target?.search(q)
            }
        }

        return binding.root
    }

    private fun getSearchTarget() : searchMarket? = when(currentFragment){
        MarkteBottom.COIN -> coinsFragment
        MarkteBottom.NEWS -> newsFragment
    }

    private fun showFragment(nav : MarkteBottom){
        val transaction = childFragmentManager.beginTransaction()

        coinsFragment?.let { transaction.hide(it) }
        newsFragment?.let { transaction.hide(it) }

        when(nav){
            MarkteBottom.COIN -> coinsFragment?.let { transaction.show(it) }
            MarkteBottom.NEWS -> newsFragment?.let { transaction.show(it) }
        }

        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding= null
    }


}