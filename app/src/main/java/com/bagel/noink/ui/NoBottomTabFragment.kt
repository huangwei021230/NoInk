package com.bagel.noink.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.bagel.noink.R

open class NoBottomTabFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bottomNavContainer : FrameLayout?= activity?.findViewById(R.id.bottom_nav_container)
        bottomNavContainer?.visibility = View.GONE

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        val bottomNavContainer : FrameLayout ?= activity?.findViewById(R.id.bottom_nav_container)
        bottomNavContainer?.visibility = View.VISIBLE
    }
}