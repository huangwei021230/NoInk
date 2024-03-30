package com.bagel.noink.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.GridLayout
import android.widget.ToggleButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bagel.noink.R
import com.bagel.noink.databinding.FragmentSearchBinding


class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!


    lateinit var gridLayout: GridLayout
    var selectedMoodTags : ArrayList<String> = ArrayList()
    var selectedEventTags : ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        var root: View = binding.root

        gridLayout = binding.moodCategory
        for (i in 0 until gridLayout.childCount) {
            val view = gridLayout.getChildAt(i)

            if (view is ToggleButton) {
                view.setOnCheckedChangeListener { _, isChecked ->
                    val tag = view.text.toString()
                    if (isChecked) {
                        selectedMoodTags.add(tag)
                    } else {
                        selectedMoodTags.remove(tag)
                    }
                }
            }
        }

        gridLayout = binding.eventCategory
        for (i in 0 until gridLayout.childCount) {
            val view = gridLayout.getChildAt(i)

            if (view is ToggleButton) {
                view.setOnCheckedChangeListener { _, isChecked ->
                    val tag = view.text.toString()
                    if (isChecked) {
                        selectedEventTags.add(tag)
                    } else {
                        selectedEventTags.remove(tag)
                    }
                }
            }
        }

        val resetButton: Button = binding.resetButton
        resetButton.setOnClickListener {
            resetSelection()
        }

        val confirmButton: Button = binding.confirmButton
        confirmButton.setOnClickListener {
            val bundle = bundleOf(
                "searchQuery" to binding.searchEditText.text.toString(),
                "moodTags" to selectedMoodTags.joinToString(","),
                "eventTags" to selectedEventTags.joinToString(",")
            )
            findNavController().navigate(R.id.action_nav_search_to_nav_search_result, bundle)
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val bundle = bundleOf(
                    "searchQuery" to binding.searchEditText.text.toString(),
                    "moodTags" to selectedMoodTags.joinToString(","),
                    "eventTags" to selectedEventTags.joinToString(",")
                )
                findNavController().navigate(R.id.action_nav_search_to_nav_search_result, bundle)
                return@setOnEditorActionListener true
            }
            false
        }

        return root
    }

    private fun resetSelection() {
        // 清空已选选项
        selectedMoodTags.clear()
        selectedEventTags.clear()

        // 更新视图状态
        updateToggleButtonStates(binding.moodCategory, selectedMoodTags)
        updateToggleButtonStates(binding.eventCategory, selectedEventTags)
    }
    private fun updateToggleButtonStates(layout: GridLayout, selectedTags: ArrayList<String>) {
        for (i in 0 until layout.childCount) {
            val view = layout.getChildAt(i)
            if (view is ToggleButton) {
                val tag = view.text.toString()
                view.isChecked = selectedTags.contains(tag)
            }
        }
    }
}