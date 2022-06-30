package com.example.tex.onBoarding

import android.os.Bundle
import android.view.View
import com.example.tex.databinding.FragmentOnboardingBinding
import com.example.tex.others.viewBinding.ViewBindingFragment
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingFragment :
    ViewBindingFragment<FragmentOnboardingBinding>(FragmentOnboardingBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = arrayListOf(
            FragmentPage1(),
            FragmentPage2(),
            FragmentPage3()
        )
        val adapter =
            OnBoardingAdapter(requireActivity().supportFragmentManager, lifecycle, fragmentList)
        binding.onBoardingViewPager.adapter = adapter
        TabLayoutMediator(binding.dotsTabLayout, binding.onBoardingViewPager) { tab, position ->

        }.attach()
    }
}