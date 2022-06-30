package com.example.tex.FragmentSaved

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tex.FragmentSaved.comments.SavedCommentsFragmentDirections
import com.example.tex.FragmentSaved.posts.SavedPostsFragmentDirections
import com.example.tex.R
import com.example.tex.databinding.FragmentSavedBinding
import com.example.tex.others.viewBinding.ViewBindingFragment
import com.google.android.material.tabs.TabLayout

class FragmentSaved :
    ViewBindingFragment<FragmentSavedBinding>(FragmentSavedBinding::inflate) {
    private val viewModel: FragmentSavedViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController by lazy {
            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.saved_fragment_container)
            navHostFragment?.findNavController()
        }
        val currentTab = binding.savedTabLayout.getTabAt(viewModel.getState())
        currentTab?.select()

        binding.savedTabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val currentTabPosition = viewModel.getState()
                if (tab?.position == 0 && currentTabPosition == 1) {
                    viewModel.setState(0)
                    navController?.navigate(SavedCommentsFragmentDirections.actionSavedCommentsFragmentToSavedPostsFragment())
                }
                if (tab?.position == 1 && currentTabPosition == 0) {
                    viewModel.setState(1)
                    navController?.navigate(SavedPostsFragmentDirections.actionSavedPostsFragmentToSavedCommentsFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }
}