package com.example.tex.SubredditProfile

import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tex.R
import com.example.tex.SubredditProfile.subredditProfileRecyclerView.SubredditCommentsRVAdapter
import com.example.tex.databinding.SubredditProfileFragmentBinding
import com.example.tex.others.viewBinding.ViewBindingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SubredditProfileFragment :
    ViewBindingFragment<SubredditProfileFragmentBinding>(SubredditProfileFragmentBinding::inflate) {
    private val args: SubredditProfileFragmentArgs by navArgs()
    private lateinit var username: String
    private var commentsAdapter: SubredditCommentsRVAdapter? = null
    private val viewModel: SubredditProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        username = args.username
        if (username.startsWith("u_")) {
            username = username.replace("u_", "")
        }
        initList()
        viewModel.setNickNameState(username)
        viewModel.getSubredditsInfo(username)

        binding.addToFriendButton.setOnClickListener {
            viewModel.setActionState(true)
            if (viewModel.getFriendState() == true) {
                binding.addToFriendButton.isEnabled = false
                viewModel.removeFromFriends(username)
            } else {
                binding.addToFriendButton.isEnabled = false
                viewModel.addToFriends(username)
            }
        }

        binding.followButton.setOnClickListener {
            binding.followButton.isEnabled = false
            viewModel.setActionState(true)
            viewModel.follow(username)
        }

        binding.unfollowButton.setOnClickListener {
            binding.unfollowButton.isEnabled = false
            viewModel.setActionState(true)
            viewModel.unFollow(username)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.flowComments.collectLatest { pagingData ->
                commentsAdapter?.submitData(pagingData)
            }
        }

        onRefreshListener()

        viewModel.subreddit.observe(viewLifecycleOwner, ::showInfo)
        viewModel.friend.observe(viewLifecycleOwner, ::addToFriends)
        viewModel.unFriend.observe(viewLifecycleOwner, ::removeFromFriends)
        viewModel.follow.observe(viewLifecycleOwner, ::follow)
        viewModel.unFollow.observe(viewLifecycleOwner, ::unFollow)
    }

    private fun initList() {
        commentsAdapter = SubredditCommentsRVAdapter()
        with(binding.userCommentsRecyclerView) {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
        commentsAdapter?.addLoadStateListener { state ->
            binding.progressBar.isVisible = state.refresh == LoadState.Loading
            if (state.refresh != LoadState.Loading && binding.refreshLayout.isRefreshing) {
                binding.userCommentsRecyclerView.scrollToPosition(0)
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    private fun showInfo(subreddit: SubredditInfo?) {
        if (subreddit != null) {
            binding.apply {
                Glide.with(requireContext())
                    .load(subreddit.avatar)
                    .placeholder(R.drawable.placeholder)
                    .into(avatarImageView)

                nicknameTextView.text = subreddit.name
                karmaTextView.text =
                    "${requireContext().getString(R.string.karma)} ${subreddit.karma}"

                if (viewModel.getFriendState() ?: subreddit.isFriend) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.setFriendState(true)
                        addToFriendButton.setImageResource(R.drawable.ic_remove_from_friends)
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.setFriendState(false)
                        addToFriendButton.setImageResource(R.drawable.ic_subscribe)
                    }
                }

                if (viewModel.getFollowState() ?: subreddit.isFollowed) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.setFollowState(true)
                        unfollowButton.isEnabled = true
                        unfollowButton.visibility = View.VISIBLE
                        followButton.isEnabled = false
                        followButton.visibility = View.INVISIBLE
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.setFollowState(false)
                        followButton.isEnabled = true
                        followButton.visibility = View.VISIBLE
                        unfollowButton.isEnabled = false
                        unfollowButton.visibility = View.INVISIBLE
                    }
                }

                addToFriendButton.isEnabled = true
                addToFriendButton.visibility = View.VISIBLE
                profileProgressBar.visibility = View.GONE
                avatarImageView.visibility = View.VISIBLE
                nicknameTextView.visibility = View.VISIBLE
                karmaTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun addToFriends(code: Int) {
        if (viewModel.getActionState()) {
            if (code == 201) {
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.User_has_been_added_to_friends),
                    Toast.LENGTH_SHORT)
                    .show()
                binding.addToFriendButton.setImageIcon(Icon.createWithResource(requireContext(),
                    R.drawable.ic_remove_from_friends))
                viewModel.setFriendState(true)
            } else {
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.User_has_not_been_added_to_friends),
                    Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.setActionState(false)
        binding.addToFriendButton.isEnabled = true
    }

    private fun removeFromFriends(code: Int) {
        if (viewModel.getActionState()) {
            if (code == 204) {
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.User_has_been_unfriended),
                    Toast.LENGTH_SHORT).show()
                binding.addToFriendButton.setImageIcon(Icon.createWithResource(requireContext(),
                    R.drawable.ic_subscribe))
                viewModel.setFriendState(false)
            } else {
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.User_has_not_been_unfriended),
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setActionState(false)
        binding.addToFriendButton.isEnabled = true
    }

    private fun follow(code: Int) {
        if (viewModel.getActionState()) {
            if (code == 200) {
                binding.apply {
                    followButton.visibility = View.INVISIBLE
                    unfollowButton.isEnabled = true
                    unfollowButton.visibility = View.VISIBLE
                    viewModel.setFollowState(true)
                }
            } else {
                binding.followButton.isEnabled = true
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.Failed_to_follow_user),
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setActionState(false)
    }

    private fun unFollow(code: Int) {
        if (viewModel.getActionState()) {
            if (code == 200) {
                binding.apply {
                    unfollowButton.visibility = View.INVISIBLE
                    followButton.isEnabled = true
                    followButton.visibility = View.VISIBLE
                    viewModel.setFollowState(false)
                }
            } else {
                binding.unfollowButton.isEnabled = true
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.Failed_to_follow_user),
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setActionState(false)
    }

    private fun onRefreshListener() {
        binding.refreshLayout.setOnRefreshListener {
            commentsAdapter?.refresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        commentsAdapter = null
    }
}