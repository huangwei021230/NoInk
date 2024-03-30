package com.bagel.noink.ui.generate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bagel.noink.R
import com.bagel.noink.databinding.FragmentLengthBinding
import com.bagel.noink.ui.account.AccountViewModel

class LengthFragment: Fragment() {
    private var _binding: FragmentLengthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
            : View? {
        // 使用 DataBindingUtil.inflate() 进行绑定
        _binding = FragmentLengthBinding.inflate(inflater,container,false)
        setNavButton()
        return binding.root
    }
    private fun setLength() {
        val includedLayout: LinearLayout = binding.linearLayout
        // 遍历 includedLayout 中的所有子视图
        val root = includedLayout.rootView
        // 遍历 GridLayout 中的每个 RelativeLayout，并获取其中的 TextView 引用
        val textView = binding.textView
        val userName = AccountViewModel.userInfo?.username ?: ""
        textView.text = "${userName}\n想写多少字的日记呢"
        var length: String? = String()
        for (i in 0 until includedLayout.childCount) {
            val child = includedLayout.getChildAt(i)
            if(child is com.shawnlin.numberpicker.NumberPicker){
                length += child.value.toString()
            }
        }
        if (length != null) {
            TextGenViewModel.updateLength(length)
        }
    }
    private fun setNavButton(){
        setLength()
        val navButton = binding.button
        navButton.setOnClickListener {
            val navController = findNavController()
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)   // 设置进入动画
                .setExitAnim(R.anim.slide_out_left)   // 设置退出动画
                .setPopEnterAnim(R.anim.slide_in_left)   // 设置返回动画
                .setPopExitAnim(R.anim.slide_out_right)   // 设置返回退出动画
                .build()
            navController.navigate(R.id.nav_textEdit, null, navOptions)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        // 解除绑定
        _binding = null
    }
}