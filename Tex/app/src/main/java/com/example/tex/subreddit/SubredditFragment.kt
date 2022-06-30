package com.example.tex.subreddit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tex.R
import com.example.tex.databinding.SubredditFragmentBinding
import com.example.tex.others.PermanentStorage
import com.example.tex.others.viewBinding.ViewBindingFragment
import com.example.tex.others.shareWithOthers
import com.example.tex.subreddit.subredditRecyclerView.ClickListenerType
import com.example.tex.subreddit.subredditRecyclerView.RedditPost
import com.example.tex.subreddit.subredditRecyclerView.SubredditAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SubredditFragment :
    ViewBindingFragment<SubredditFragmentBinding>(SubredditFragmentBinding::inflate) {
    var subredditAdapter: SubredditAdapter? = null
    val viewModel: SubredditViewModel by viewModels()
    private var subredditPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("access token", PermanentStorage.token)
        onInit()
        viewModel.unfollow.observe(viewLifecycleOwner, ::unfollow)
        viewModel.follow.observe(viewLifecycleOwner, ::follow)
        viewModel.searchPosts.observe(viewLifecycleOwner, ::showSearchResults)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            if (!viewModel.getSearchState()) {
                if (viewModel.getTabState() == 0) {
                    viewModel.flowNewPost.collectLatest { pagingData ->
                        subredditAdapter?.submitData(pagingData)
                    }
                } else {
                    viewModel.flowHotPost.collectLatest { pagingData ->
                        subredditAdapter?.submitData(pagingData)
                    }
                }
            }
        }
        viewModel.getUsersName()
        binding.subredditRecyclerView.addOnScrollHiddenView(binding.linearLayout, 200.toFloat())
    }

    private fun initList() {
        subredditAdapter = SubredditAdapter()
        with(binding.subredditRecyclerView) {
            adapter = subredditAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
        }
        subredditAdapter?.itemClickListener = { item, position, type, name ->
            onClickListener(item, type, position, name)
        }
        subredditAdapter?.addLoadStateListener { state ->
            binding.progressBar.isVisible = state.refresh == LoadState.Loading
            if (state.refresh != LoadState.Loading && binding.refreshLayout.isRefreshing) {
                binding.subredditRecyclerView.scrollToPosition(0)
                binding.refreshLayout.isRefreshing = false
            }
        }
        binding.subredditRecyclerView.itemAnimator = null
    }

    private fun onInit() {
        initList()
        onTabSelectedListener()
        onSearch()
        onRefreshListener()
    }

    private fun onSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    subredditAdapter?.submitData(lifecycle, PagingData.empty())
                    binding.tabLayout.visibility = View.GONE
                    viewModel.setSearchState(true)
                    viewModel.searchPosts(query)
                    return false
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun showSearchResults(results: PagingData<RedditPost>) {
        lifecycleScope.launch {
            if (viewModel.getSearchState()) {
                binding.tabLayout.visibility = View.GONE
                subredditAdapter?.submitData(results)
                viewModel.setSearchState(false)
            }
        }
    }

    private fun onClickListener(
        item: RedditPost,
        type: ClickListenerType,
        position: Int,
        name: String,
    ) {
        Log.e("tupe", type.toString())
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
            ClickListenerType.NAVIGATE -> navigateToDetailedFragment(item)
            ClickListenerType.SHARE -> shareWithOthers(item.url, requireContext())
            ClickListenerType.PROFILE -> navigateToProfile(item)
        }
    }

    private fun onTabSelectedListener() {
        val currentTab = binding.tabLayout.getTabAt(viewModel.getTabState())
        currentTab?.select()
        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    viewModel.setTabState(0)
                    subredditAdapter?.submitData(lifecycle, PagingData.empty())
                    lifecycleScope.launch {
                        viewModel.flowNewPost.collectLatest { pagingData ->
                            subredditAdapter?.submitData(pagingData)
                        }
                    }
                } else {
                    viewModel.setTabState(1)
                    subredditAdapter?.submitData(lifecycle, PagingData.empty())
                    lifecycleScope.launch {
                        viewModel.flowHotPost.collectLatest { pagingData ->
                            subredditAdapter?.submitData(pagingData)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }


    private fun navigateToDetailedFragment(item: RedditPost) {
        when (item) {
            is RedditPost.ImagePost -> findNavController().navigate(
                SubredditFragmentDirections.actionSubredditFragmentToImageDetailedPostFragment(item))
            is RedditPost.SimplePost -> findNavController().navigate(
                SubredditFragmentDirections.actionSubredditFragmentToSimpleDetailedPostFragment(item))
            is RedditPost.VideoPost -> findNavController().navigate(
                SubredditFragmentDirections.actionSubredditFragmentToVideoDetailedPostFragment(item))
        }
    }

    private fun RecyclerView.addOnScrollHiddenView(
        hiddenView: View,
        translationX: Float = 0F,
        translationY: Float = 0F,
        duration: Long = 200L,
    ) {
        var isViewShown = true
        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when {
                    dy > 0 && isViewShown -> {
                        isViewShown = false
                        hiddenView.animate()
                            .translationX(-translationY)
                            .translationY(-translationX)
                            .duration = duration
                    }
                    dy < 0 && !isViewShown -> {
                        isViewShown = true
                        hiddenView.animate()
                            .translationX(0f)
                            .translationY(0f)
                            .duration = duration
                    }
                }
            }
        })
    }

    private fun unfollow(code: Int) {
        if (code == 200) {
            lifecycleScope.launch(Dispatchers.Default) {
                val newList = mutableListOf<RedditPost>()
                subredditAdapter?.snapshot()?.items?.forEach {
                    if (it.nickName == viewModel.getSubredditNameState()) {
                        it.isFollowed = false
                        newList.add(it)
                    } else {
                        newList.add(it)
                    }
                }
                var itemList = subredditAdapter?.snapshot()?.items as MutableList<RedditPost>
                itemList = newList
                val lm = binding.subredditRecyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = lm.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = lm.findLastVisibleItemPosition()
                subredditAdapter?.notifyItemRangeChanged(firstVisibleItemPosition,
                    lastVisibleItemPosition)
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
                subredditAdapter?.snapshot()?.items?.forEach {
                    if (it.nickName == viewModel.getSubredditNameState()) {
                        it.isFollowed = true
                        newList.add(it)
                    } else {
                        newList.add(it)
                    }
                }
                var itemList = subredditAdapter?.snapshot()?.items as MutableList<RedditPost>
                itemList = newList
                val lm = binding.subredditRecyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = lm.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = lm.findLastVisibleItemPosition()
                subredditAdapter?.notifyItemRangeChanged(firstVisibleItemPosition,
                    lastVisibleItemPosition)
            }
        } else {
            Toast.makeText(requireContext(),
                requireContext().getString(R.string.failed_to_follow),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun onRefreshListener() {
        binding.refreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                subredditAdapter?.refresh()
            }
        }
    }

    private fun navigateToProfile(item: RedditPost) {
        if (item.prefix.startsWith("u")) {
            findNavController().navigate(SubredditFragmentDirections.actionSubredditFragmentToSubredditProfileFragment(
                item.nickName))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subredditAdapter = null
    }
}
