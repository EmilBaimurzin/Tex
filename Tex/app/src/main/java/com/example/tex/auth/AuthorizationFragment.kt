package com.example.tex.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tex.R
import com.example.tex.databinding.AuthorizationFragmentBinding
import com.example.tex.others.PermanentStorage
import com.example.tex.others.viewBinding.ViewBindingFragment

class AuthorizationFragment :
    ViewBindingFragment<AuthorizationFragmentBinding>(AuthorizationFragmentBinding::inflate) {
    private val viewModel: AuthorizationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PermanentStorage.token != "") {
            viewModel.toSharedPrefs()
            findNavController().navigate(R.id.action_authorizationFragment_to_navigationFragment)
        }

        binding.authButton.setOnClickListener {
            findNavController().navigate(R.id.action_authorizationFragment_to_fragmentWeb)
        }

        binding.registrationButton.setOnClickListener {
            val browseIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.reddit.com/register/"))
            startActivity(browseIntent)
        }
    }
}
