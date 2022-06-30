package com.example.tex.NavigationFragment

import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.tex.databinding.NavigationFragmentBinding
import com.example.tex.others.viewBinding.ViewBindingFragment

class NavigationFragment :
    ViewBindingFragment<NavigationFragmentBinding>(NavigationFragmentBinding::inflate) {
    override fun onStart() {
        super.onStart()
        val navController = Navigation.findNavController(binding.navFragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }
}