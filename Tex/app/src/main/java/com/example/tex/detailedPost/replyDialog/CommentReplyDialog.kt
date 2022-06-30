package com.example.tex.detailedPost.replyDialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tex.R
import com.example.tex.databinding.ReplyDialogBinding
import com.example.tex.detailedPost.detailedPostRecyclerView.DetailedPostComment
import com.example.tex.others.viewBinding.ViewBindingBottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class CommentReplyDialog : ViewBindingBottomSheetDialog<ReplyDialogBinding>(ReplyDialogBinding::inflate) {
    private val args: CommentReplyDialogArgs by navArgs()
    private lateinit var item: DetailedPostComment
    private val viewModel: CommentReplyViewModel by viewModels()

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
        item = args.item
        setAttributes()
        setOnTextChangedListener()
        binding.commentEditText.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        binding.sendCommentButton.setOnClickListener {
            viewModel.setSendAction(true)
            viewModel.sendComment(item.commentId, binding.commentEditText.text.toString())
        }

        viewModel.comment.observe(viewLifecycleOwner, ::commentResult)
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

    private fun setAttributes() {
        binding.apply {
            nicknameTextView.text = item.nickname
            timeTextView.text = item.time
            descriptionTextView.text = item.description
        }
    }

    private fun commentResult(value: Boolean) {
        if (viewModel.getSendAction()) {
            if (value) {
                Toast.makeText(requireContext(), R.string.sent_successfully, Toast.LENGTH_SHORT)
                    .show()

                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(),
                    R.string.error_reply_was_not_added,
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.setSendAction(false)
    }
}