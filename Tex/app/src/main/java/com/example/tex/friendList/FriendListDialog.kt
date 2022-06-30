package com.example.tex.friendList

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tex.databinding.FriendListDialogBinding
import com.example.tex.others.viewBinding.ViewBindingBottomSheetDialog
import com.example.tex.profileFragment.ProfileFragmentDirections
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class FriendListDialog :
    ViewBindingBottomSheetDialog<FriendListDialogBinding>(FriendListDialogBinding::inflate) {
    private val viewModel: FriendListViewModel by viewModels()
    private var friends = ArrayList<String>()
    private lateinit var friendsAdapter: ArrayAdapter<String>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
            }
        }
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            friends
        )

        binding.friendsList.adapter = friendsAdapter

        binding.friendsList.onItemClickListener =
            OnItemClickListener { _, _, itemPosition, _ ->
                findNavController().navigateUp()
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSubredditProfileFragment(
                    friends[itemPosition]))
            }

        viewModel.getFriends()
        viewModel.friends.observe(viewLifecycleOwner, ::getFriends)
    }

    private fun getFriends(list: List<String>) {
        if (list.isNotEmpty()) {
            binding.progressBar.visibility = View.GONE
            binding.friendsList.visibility = View.VISIBLE
            list.forEach {
                friends.add(it)
            }
            friendsAdapter.notifyDataSetChanged()
        } else {
            binding.friendsList.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.friendListTextView.visibility = View.VISIBLE
        }
    }
}