package com.example.tex.onBoarding

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tex.R

class FragmentPage3: Fragment(R.layout.onboarding_page_3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val getStartedButton = view.findViewById<Button>(R.id.getStartedButton)
        getStartedButton.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_authorizationFragment)
        }
    }
}