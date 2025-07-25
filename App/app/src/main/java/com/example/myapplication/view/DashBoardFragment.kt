package com.example.myapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDashBoardBinding
import com.example.myapplication.viewModel.Bottom
import com.example.myapplication.viewModel.BottomNav_ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.activity.OnBackPressedCallback
import java.util.Objects

class DashBoardFragment : Fragment() {

    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomNav: BottomNavigationView

    private lateinit var bottomNav_ViewModel: BottomNav_ViewModel

    private lateinit var currentFragment : Bottom

    private var homeFragment : HomeFragment? = null
    private var analyticsFragment : AnalyticsFragment? = null
    private var digitalAssetsFragment : DigitalAssestsFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment]
        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)

        bottomNav_ViewModel = ViewModelProvider(this)[BottomNav_ViewModel::class.java]

        val currentnav = bottomNav_ViewModel.BottomNav.value

        val cm = childFragmentManager

        if (homeFragment == null){
            homeFragment = HomeFragment()
            cm.beginTransaction()
                .add(R.id.FrameLayoutDashBoard,homeFragment!!,"homeFragment")
                .commit()
        }

        if (analyticsFragment == null){
            analyticsFragment = AnalyticsFragment()
            cm.beginTransaction()
                .add(R.id.FrameLayoutDashBoard,analyticsFragment!!,"analyticsFragment")
                .commit()
        }

        if (digitalAssetsFragment == null){
            digitalAssetsFragment = DigitalAssestsFragment()
            cm.beginTransaction()
                .add(R.id.FrameLayoutDashBoard,digitalAssetsFragment!!,"digitalAssetsFragment")
                .commit()
        }

        currentFragment = bottomNav_ViewModel.BottomNav.value ?: Bottom.HOME
        showFragment(currentFragment)

        bottomNav = binding.bottomNav


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentFragment != Bottom.HOME) {
                    bottomNav_ViewModel.bottomChoosen(Bottom.HOME)
                    binding.bottomNav.selectedItemId = R.id.Bottom_Nav_Home
                } else {
                    requireActivity().finish()
                }
            }
        })


        bottomNav_ViewModel.BottomNav.observe(viewLifecycleOwner){nav->

            if (nav != currentFragment){
                currentFragment = nav
                showFragment(nav)
            }

        }

        bottomNav.setOnItemSelectedListener {
            when(it.itemId)
            {
                R.id.Bottom_Nav_Home -> {
                    bottomNav_ViewModel.bottomChoosen(Bottom.HOME)
                    true
                }
                R.id.Bottom_Nav_Analytics ->
                {
                    bottomNav_ViewModel.bottomChoosen(Bottom.ANALYTICS)
                    true
                }
                R.id.Bottom_Nav_DigitalAssests ->
                {
                    bottomNav_ViewModel.bottomChoosen(Bottom.NFTS)
                    true
                }

                else -> {
                    false
                }
            }
        }

        bottomNav.setOnItemReselectedListener {  }



        return binding.root
    }

    private fun showFragment(nav : Bottom){
        val transaction = childFragmentManager.beginTransaction()

        homeFragment?.let { transaction.hide(it) }
        analyticsFragment?.let { transaction.hide(it) }
        digitalAssetsFragment?.let { transaction.hide(it) }

        when(nav){
            Bottom.HOME -> homeFragment?.let { transaction.show(it) }
            Bottom.ANALYTICS -> analyticsFragment?.let { transaction.show(it) }
            Bottom.NFTS -> digitalAssetsFragment?.let { transaction.show(it) }
        }

        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}