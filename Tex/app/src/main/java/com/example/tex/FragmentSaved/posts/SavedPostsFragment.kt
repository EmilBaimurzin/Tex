package com.example.tex.FragmentSaved.posts

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tex.R
import com.example.tex.databinding.SavedPostsFragmentBinding
import com.example.tex.others.shareWithOthers
import com.example.tex.others.viewBinding.ViewBindingFragment
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            if (viewModel.getPageState()) {
                viewModel.flowSavedPost.collectLatest { pagingData ->
                    savedPostsAdapter?.submitData(pagingData)
                }
            } else {
                viewModel.flowLocalPost.collectLatest { pagingData ->
                    savedPostsAdapter?.submitData(pagingData)
                }
            }
        }

        if (viewModel.getPageState()) {
            binding.switchPageButton.setImageResource(R.drawable.ic_local)
        } else {
            binding.switchPageButton.setImageResource(R.drawable.ic_save)
        }

        binding.switchPageButton.setOnClickListener {
            changeList()
        }

        viewModel.unfollow.observe(viewLifecycleOwner, ::unfollow)
        viewModel.follow.observe(viewLifecycleOwner, ::follow)
        viewModel.unsaveOnline.observe(viewLifecycleOwner, ::unsave)
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
                viewModel.setActionState(true)
                viewModel.setSubredditNameState(name)
                viewModel.setPositionState(position)
                viewModel.follow(name)
            }
            ClickListenerType.UNFOLLOW -> {
                viewModel.setActionState(true)
                viewModel.setSubredditNameState(name)
                viewModel.setPositionState(position)
                viewModel.unfollow(name)
            }
            ClickListenerType.NAVIGATE -> unsavePost(item)
            ClickListenerType.SHARE -> shareWithOthers(item.url, requireContext())
            ClickListenerType.PROFILE -> {}
            ClickListenerType.SAVE -> {}
            ClickListenerType.UNSAVE -> {
                viewModel.setActionState(true)
                viewModel.unsavePostOnline(item)
            }
        }
    }

    private fun unfollow(code: Int) {
        if (viewModel.getActionState()) {
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
                    val lm = binding.savedPostsRecyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = lm.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = lm.findLastVisibleItemPosition()
                    savedPostsAdapter?.notifyItemRangeChanged(firstVisibleItemPosition,
                        lastVisibleItemPosition)
                    savedPostsAdapter?.notifyItemChanged(viewModel.getPositionState())
                }
            } else {
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.failed_to_unfollow),
                    Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.setActionState(false)
    }

    private fun follow(code: Int) {
        if (viewModel.getPageState()) {
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
                    val lm = binding.savedPostsRecyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = lm.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition = lm.findLastVisibleItemPosition()
                    savedPostsAdapter?.notifyItemRangeChanged(firstVisibleItemPosition,
                        lastVisibleItemPosition)
                    savedPostsAdapter?.notifyItemChanged(viewModel.getPositionState())
                }
            } else {
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.failed_to_follow),
                    Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.setActionState(false)
    }

    private fun unsavePost(item: RedditPost) {
        val deleteItemString = requireContext().getString(R.string.delete_item)
        val confirmString = requireContext().getString(R.string.confirm)
        val cancelString = requireContext().getString(R.string.cancel)
        if (item.isLocal) {
            viewModel.setActionState(true)
            viewModel.unsavePostLocal(item.postId)
            AlertDialog.Builder(requireContext())
                .setTitle(deleteItemString)
                .setPositiveButton(confirmString) { dialog, _ ->
                    unsave(true)
                    dialog.cancel()
                }
                .setNegativeButton(cancelString) { dialog, _ ->
                    dialog.cancel()
                }
                .create()
                .show()
        }
    }

    private fun changeList() {
        if (viewModel.getPageState()) {
            lifecycleScope.launch {
                savedPostsAdapter?.submitData(lifecycle, PagingData.empty())
                viewModel.flowLocalPost.collectLatest { pagingData ->
                    savedPostsAdapter?.submitData(pagingData)
                }
            }
            viewModel.setPageState(false)
            binding.switchPageButton.setImageResource(R.drawable.ic_save)
        } else {
            lifecycleScope.launch {
                savedPostsAdapter?.submitData(lifecycle, PagingData.empty())
                viewModel.flowSavedPost.collectLatest { pagingData ->
                    savedPostsAdapter?.submitData(pagingData)
                }
            }
            viewModel.setPageState(true)
            binding.switchPageButton.setImageResource(R.drawable.ic_local)
        }
    }

    private fun unsave(value: Boolean) {
        if (viewModel.getActionState()) {
            if (value) {
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.unsaved_successfully),
                    Toast.LENGTH_SHORT).show()
                savedPostsAdapter?.refresh()
            } else {
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.unable_to_unsave),
                    Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.setActionState(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        savedPostsAdapter = null
    }
}