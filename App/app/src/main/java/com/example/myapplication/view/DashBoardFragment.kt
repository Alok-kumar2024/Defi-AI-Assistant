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

class DashBoardFragment : Fragment() {

    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomNav: BottomNavigationView

    private lateinit var bottomNav_ViewModel: BottomNav_ViewModel

    private lateinit var currentFragment : Bottom

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment]
        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)

        bottomNav_ViewModel = ViewModelProvider(this)[BottomNav_ViewModel::class.java]

        val currentnav = bottomNav_ViewModel.BottomNav.value

        if (currentnav != null)
        {
            currentFragment = currentnav

            val fragment = when(currentFragment)
            {
                Bottom.HOME -> HomeFragment()
                Bottom.ANALYTICS -> AnalyticsFragment()
                Bottom.NFTS -> DigitalAssestsFragment()
            }
            switchFragment(fragment)
        }else
        {
            bottomNav_ViewModel.bottomChoosen(Bottom.HOME)
        }

        bottomNav = binding.bottomNav


        bottomNav_ViewModel.BottomNav.observe(viewLifecycleOwner){nav->
            currentFragment = nav

            when(nav)
            {
                Bottom.HOME -> switchFragment(HomeFragment())
                Bottom.ANALYTICS -> switchFragment(AnalyticsFragment())
                Bottom.NFTS -> switchFragment(DigitalAssestsFragment())
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


        return binding.root
    }

    fun switchFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.FrameLayoutDashBoard, fragment)
            .commit()
    }

}