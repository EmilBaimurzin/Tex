package com.example.tex.FragmentSaved.posts

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tex.R
import com.example.tex.databinding.SavedPostsFragmentBinding
import com.example.tex.others.viewBinding.ViewBindingFragment
import com.example.tex.others.shareWithOthers
import com.example.tex.subreddit.subredditRecyclerView.ClickListenerType
import com.example.tex.subreddit.subredditRecyclerView.RedditPost
import com.example.tex.subreddit.subredditRecyclerView.SubredditAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedPostsFragment :
    ViewBindingFragment<SavedPostsFragmentBinding>(SavedPostsFragmentBinding::inflate) {
    private var savedPostsAdapter: SubredditAdapter? = null
    private val viewModel: SavedPostsViewModel by viewModels()
    private var subredditPosition = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.flowSavedPost.collectLatest { pagingData ->
                savedPostsAdapter?.submitData(pagingData)
            }
        }
        viewModel.unfollow.observe(viewLifecycleOwner, ::unfollow)
        viewModel.follow.observe(viewLifecycleOwner, ::follow)
        onRefreshListener()
    }

    private fun initList() {
        savedPostsAdapter = SubredditAdapter()
        with(binding.savedPostsRecyclerView) {
            adapter = savedPostsAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
        savedPostsAdapter?.itemClickListener = { item, position, type, name ->
            onClickListener(item, type, position, name)
        }

        binding.savedPostsRecyclerView.itemAnimator = null

        savedPostsAdapter?.addLoadStateListener { state ->
            binding.savedPostsProgressBar.isVisible = state.refresh == LoadState.Loading
            if (state.refresh != LoadState.Loading && binding.refreshLayout.isRefreshing) {
                binding.savedPostsRecyclerView.smoothScrollToPosition(0)
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    private fun onRefreshListener() {
        binding.refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                savedPostsAdapter?.refresh()
            }
        }
    }

    private fun onClickListener(
        item: RedditPost,
        type: ClickListenerType,
        position: Int,
        name: String,
    ) {
        when (type) {
            ClickListenerType.FOLLOW -> {
                viewModel.setSubredditNameState(name)
                subredditPosition = position
                viewModel.follow(name)
            }
            ClickListenerType.UNFOLLOW -> {
                viewModel.setSubredditNameState(name)
                subredditPosition = position
                viewModel.unfollow(name)
            }
            ClickListenerType.NAVIGATE -> {}
            ClickListenerType.SHARE -> shareWithOthers(item.url, requireContext())
            ClickListenerType.PROFILE -> {}
        }
    }

    private fun unfollow(code: Int) {
        if (code == 200) {
            lifecycleScope.launch(Dispatchers.Default) {
                val newList = mutableListOf<RedditPost>()
                savedPostsAdapter?.snapshot()?.items?.forEach {
                    if (it.nickName == viewModel.getSubredditNameState()) {
                        it.isFollowed = false
                        newList.add(it)
                    } else {
                        newList.add(it)
                    }
                }
                var itemList = savedPostsAdapter?.snapshot()?.items as MutableList<RedditPost>
                itemList = newList
                savedPostsAdapter?.notifyItemChanged(subredditPosition)
            }
        } else {
            Toast.makeText(requireContext(),
                requireContext().getString(R.string.failed_to_unfollow),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun follow(code: Int) {
        if (code == 200) {
            lifecycleScope.launch(Dispatchers.Default) {
                val newList = mutableListOf<RedditPost>()
                savedPostsAdapter?.snapshot()?.items?.forEach {
                    if (it.nickName == viewModel.getSubredditNameState()) {
                        it.isFollowed = true
                        newList.add(it)
                    } else {
                        newList.add(it)
                    }
                }
                var itemList = savedPostsAdapter?.snapshot()?.items as MutableList<RedditPost>
                itemList = newList
                savedPostsAdapter?.notifyItemChanged(subredditPosition)
            }
        } else {
            Toast.makeText(requireContext(),
                requireContext().getString(R.string.failed_to_follow),
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        savedPostsAdapter = null
    }
}