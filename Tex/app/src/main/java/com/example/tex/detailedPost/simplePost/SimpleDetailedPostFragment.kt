package com.example.tex.detailedPost.simplePost

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tex.R
import com.example.tex.databinding.SimpleDetailedPostFragmentBinding
import com.example.tex.detailedPost.detailedPostRecyclerView.CommentClickListenerType
import com.example.tex.detailedPost.detailedPostRecyclerView.DetailedCommentAdapter
import com.example.tex.others.viewBinding.ViewBindingFragment
import com.example.tex.others.shareWithOthers
import com.example.tex.subreddit.subredditRecyclerView.RedditPost
import kotlinx.coroutines.flow.collectLatest

class SimpleDetailedPostFragment :
    ViewBindingFragment<SimpleDetailedPostFragmentBinding>(SimpleDetailedPostFragmentBinding::inflate) {
    private val args: SimpleDetailedPostFragmentArgs by navArgs()
    private val viewModel: SimpleDetailedPostViewModel by viewModels()
    private lateinit var item: RedditPost.SimplePost
    private var commentAdapter: DetailedCommentAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.commentsRecyclerView.isNestedScrollingEnabled = true
        item = args.item
        viewModel.setLink(item.url)
        setAttributes()
        initList()
        setOnTextChangedListener()


        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.flowComments.collectLatest { pagingData ->
                commentAdapter?.submitData(pagingData)
            }
        }

        binding.sendCommentButton.setOnClickListener {
            viewModel.setSendAction(true)
            viewModel.sendComment(binding.commentEditText.text.toString(), item.postId)
        }

        binding.shareButton.setOnClickListener {
            shareWithOthers(item.url, requireContext())
        }

        binding.subscribeButton.setOnClickListener {
            viewModel.setFollowObserveAction(true)
            if (viewModel.getFollowAction() == true) {
                viewModel.unfollow(item.nickName)
            } else {
                viewModel.follow(item.nickName)
            }
        }

        viewModel.save.observe(viewLifecycleOwner, ::save)
        viewModel.unSave.observe(viewLifecycleOwner, ::unsave)
        viewModel.comment.observe(viewLifecycleOwner, ::commentResult)
        viewModel.follow.observe(viewLifecycleOwner, ::follow)
        viewModel.unFollow.observe(viewLifecycleOwner, ::unFollow)
    }

    private fun setAttributes() {
        binding.apply {
            Glide.with(requireContext())
                .load(item.avatar)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.placeholder)
                .into(avatarImageView)
            nicknameTextView.text = item.nickName
            timeTextView.text = item.time
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            commentsTextView.text = item.comments

            if (viewModel.getFollowAction() == null) {
                viewModel.setFollowAction(item.isFollowed)
                if (item.isFollowed) {
                    subscribeButton.setImageResource(R.drawable.ic_baseline_done_24)
                } else {
                    subscribeButton.setImageResource(R.drawable.ic_add)
                }
            } else {
                if (viewModel.getFollowAction() == true) {
                    subscribeButton.setImageResource(R.drawable.ic_baseline_done_24)
                } else {
                    subscribeButton.setImageResource(R.drawable.ic_add)
                }
            }
        }
    }

    private fun initList() {
        commentAdapter = DetailedCommentAdapter()
        with(binding.commentsRecyclerView) {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            setHasFixedSize(false)
        }
        commentAdapter?.addLoadStateListener { state ->
            binding.commentsProgressBar.isVisible = state.refresh == LoadState.Loading
        }

        binding.commentsRecyclerView.itemAnimator = null

        commentAdapter?.itemClickListener = { item, position, vote, type ->
            when (type) {
                CommentClickListenerType.VOTE -> when {
                    vote == 1 && item.likes == true -> {
                        viewModel.vote(item.commentId, 0)
                        commentAdapter?.snapshot()?.items!![position].likes = null
                        commentAdapter?.snapshot()?.items!![position].ups--
                        commentAdapter?.notifyItemChanged(position)
                    }
                    (vote == 1 && item.likes == null) -> {
                        viewModel.vote(item.commentId, 1)
                        commentAdapter?.snapshot()?.items!![position].likes = true
                        commentAdapter?.snapshot()?.items!![position].ups++
                        commentAdapter?.notifyItemChanged(position)
                    }

                    (vote == 1 && item.likes == false) -> {
                        viewModel.vote(item.commentId, 1)
                        commentAdapter?.snapshot()?.items!![position].likes = true
                        commentAdapter?.snapshot()?.items!![position].ups =
                            commentAdapter?.snapshot()?.items!![position].ups + 2
                        commentAdapter?.notifyItemChanged(position)
                    }

                    vote == -1 && item.likes == false -> {
                        viewModel.vote(item.commentId, 0)
                        commentAdapter?.snapshot()?.items!![position].likes = null
                        commentAdapter?.snapshot()?.items!![position].ups++
                        commentAdapter?.notifyItemChanged(position)
                    }
                    vote == -1 && item.likes == null -> {
                        viewModel.vote(item.commentId, -1)
                        commentAdapter?.snapshot()?.items!![position].likes = false
                        commentAdapter?.snapshot()?.items!![position].ups--
                        commentAdapter?.notifyItemChanged(position)
                    }

                    vote == -1 && item.likes == true -> {
                        viewModel.vote(item.commentId, -1)
                        commentAdapter?.snapshot()?.items!![position].likes = false
                        commentAdapter?.snapshot()?.items!![position].ups =
                            commentAdapter?.snapshot()?.items!![position].ups - 2
                        commentAdapter?.notifyItemChanged(position)
                    }
                }

                CommentClickListenerType.REPLY -> findNavController().navigate(
                    SimpleDetailedPostFragmentDirections.actionSimpleDetailedPostFragmentToCommentReplyDialog(
                        item))
                CommentClickListenerType.SAVE -> {
                    viewModel.saveComment(Pair(item.commentId, position))
                    viewModel.setSaveAction(true)
                }
                CommentClickListenerType.UNSAVE -> {
                    viewModel.unSaveComment(Pair(item.commentId, position))
                    viewModel.setSaveAction(true)
                }
                CommentClickListenerType.PROFILE -> {
                    findNavController().navigate(SimpleDetailedPostFragmentDirections.actionSimpleDetailedPostFragmentToSubredditProfileFragment(
                        item.nickname))
                }
            }
        }
    }

    private fun setOnTextChangedListener() {
        binding.commentEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.commentEditText.text?.isNotBlank() == true) {
                    binding.sendCommentButton.setColorFilter(Color.rgb(25, 118, 210))
                    binding.sendCommentButton.isEnabled = true
                } else {
                    binding.sendCommentButton.isEnabled = false
                    binding.sendCommentButton.setColorFilter(Color.rgb(189, 189, 189))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun unsave(pairValuePosition: Pair<Boolean, Int>) {
        if (pairValuePosition.first && viewModel.getSaveAction()) {
            commentAdapter?.snapshot()?.items!![pairValuePosition.second].isSaved = false
            commentAdapter?.notifyItemChanged(pairValuePosition.second)
            Toast.makeText(requireContext(), R.string.unsaved_successfully, Toast.LENGTH_SHORT)
                .show()
        } else {
            if (viewModel.getSaveAction()) {
                Toast.makeText(requireContext(), R.string.unable_to_unsave, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setSaveAction(false)
    }

    private fun save(pairValuePosition: Pair<Boolean, Int>) {
        if (pairValuePosition.first && viewModel.getSaveAction()) {
            commentAdapter?.snapshot()?.items!![pairValuePosition.second].isSaved = true
            commentAdapter?.notifyItemChanged(pairValuePosition.second)
            Toast.makeText(requireContext(), R.string.saved_successfully, Toast.LENGTH_SHORT)
                .show()
        } else {
            if (viewModel.getSaveAction()) {
                Toast.makeText(requireContext(), R.string.unable_to_save, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setSaveAction(false)
    }

    private fun commentResult(value: Boolean) {
        if (viewModel.getSendAction()) {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            if (value) {
                binding.commentEditText.setText("")
                commentAdapter?.refresh()
                Toast.makeText(requireContext(),
                    R.string.comment_has_been_added,
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),
                    R.string.error_comment_was_not_added,
                    Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.setSendAction(false)
    }

    private fun follow(value: Boolean) {
        if (viewModel.getFollowObserveAction() == true) {
            if (value) {
                viewModel.setFollowAction(true)
                binding.subscribeButton.setImageResource(R.drawable.ic_baseline_done_24)
            } else {
                Toast.makeText(requireContext(), R.string.failed_to_follow, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setFollowObserveAction(false)
    }

    private fun unFollow(value: Boolean) {
        if (viewModel.getFollowObserveAction() == true) {
            if (value) {
                viewModel.setFollowAction(false)
                binding.subscribeButton.setImageResource(R.drawable.ic_add)
            } else {
                Toast.makeText(requireContext(), R.string.failed_to_follow, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setFollowObserveAction(false)
    }
}