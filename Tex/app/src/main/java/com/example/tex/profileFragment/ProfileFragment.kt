package com.example.tex.profileFragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.tex.R
import com.example.tex.databinding.ProfileFragmentBinding
import com.example.tex.others.PermanentStorage
import com.example.tex.others.viewBinding.ViewBindingFragment

class ProfileFragment :
    ViewBindingFragment<ProfileFragmentBinding>(ProfileFragmentBinding::inflate) {
    private val viewModel: ProfileViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()

        binding.logOutButton.setOnClickListener { logOut() }

        binding.friendListButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_friendListDialog)
        }
        viewModel.userInfo.observe(viewLifecycleOwner, ::getUserInfo)
    }

    private fun getUserInfo(info: UserInfo?) {
        if (info != null) {
            val circularProgressDrawable = CircularProgressDrawable(requireContext())
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            Glide.with(requireContext())
                .load(info.avatar)
                .placeholder(circularProgressDrawable)
                .into(binding.avatarImageView)

            binding.progressBar.visibility = View.GONE
            binding.friendNumberTextView.text =
                "${requireContext().getString(R.string.friends)} ${info.friendNumber}"
            binding.nameTextView.text = info.name
        } else {
            Toast.makeText(requireContext(),
                requireContext().getString(R.string.unable_to_get_data),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun logOut() {
        PermanentStorage.token = ""
        val navController = Navigation.findNavController(requireActivity(), R.id.fragment)
        navController.navigate(R.id.action_navigationFragment_to_mainFragment)
    }
}