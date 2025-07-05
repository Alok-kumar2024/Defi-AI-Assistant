package com.example.myapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDigitalAssestsBinding

class DigitalAssestsFragment : Fragment() {

    private var _binding : FragmentDigitalAssestsBinding ?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDigitalAssestsBinding.inflate(inflater,container,false)




        return  binding.root
    }

}