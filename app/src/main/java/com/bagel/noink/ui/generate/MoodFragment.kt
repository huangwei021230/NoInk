package com.bagel.noink.ui.generate

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bagel.noink.R
import com.bagel.noink.databinding.FragmentMoodBinding
import com.bagel.noink.databinding.MoodBinding
import com.bagel.noink.ui.NoBottomTabFragment
import com.bagel.noink.ui.account.AccountViewModel

class MoodFragment : NoBottomTabFragment() {
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!

    val backgroundDrawable = StateListDrawable()
    private lateinit var defaultBackground: Drawable
    private lateinit var pressedBackground: Drawable

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        val root: View = binding.root        // 找到 TextView

        val textView: TextView = binding.textView
        val username = AccountViewModel.userInfo?.username ?: ""
        val message = "那么${username}\n这一天的心情是怎样的呢"
        textView.text = message

        defaultBackground = requireContext().resources.getDrawable(R.drawable.default_background)
        pressedBackground = requireContext().resources.getDrawable(R.drawable.pressed_background)
        val includedLayout: View = root.findViewById(R.id.includedLayout)
        setClickListener()
        setNavButton()
        return root
    }

//    private fun setClickListener() {
//        val includedLayout: MoodBinding = binding.includedLayout
//        // 遍历 includedLayout 中的所有子视图
//        val root = includedLayout.root
//        // 遍历 GridLayout 中的每个 RelativeLayout，并获取其中的 TextView 引用
//        val gridLayout = root.findViewById<GridLayout>(R.id.mood_gridlayout) // 替换为您的 GridLayout ID
//        for (i in 0 until gridLayout.childCount) {
//            val child = gridLayout.getChildAt(i)
//            if (child is RelativeLayout) {
//                for(j in 0 until child.childCount){
//                    val textView = child.getChildAt(j)
//                    if(textView is ToggleButton) {
//                        textView.setOnCheckedChangeListener { _, isChecked ->
//                            if (isChecked) {
//                                textView.background = pressedBackground
//                                val text:String = textView.text.toString()
//                                TextGenViewModel.updateStyle(text)
//                            } else {
//                                textView.background = defaultBackground
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun setClickListener() {
        val includedLayout: MoodBinding = binding.includedLayout
        // 遍历 includedLayout 中的所有子视图
        val root = includedLayout.root
        // 遍历 GridLayout 中的每个 RelativeLayout，并获取其中的 TextView 引用
        val gridLayout = root.findViewById<GridLayout>(R.id.mood_gridlayout) // 替换为您的 GridLayout ID
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            if (child is RelativeLayout) {
                for (j in 0 until child.childCount) {
                    val textView = child.getChildAt(j)
                    if (textView is ToggleButton) {
                        textView.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) {
                                // 当前 ToggleButton 被选中
                                textView.background = pressedBackground
                                val text: String = textView.text.toString()
                                TextGenViewModel.updateStyle(text)

                                // 遍历其他 ToggleButton 并将它们的状态设置为非选中
                                for (k in 0 until gridLayout.childCount) {
                                    val otherChild = gridLayout.getChildAt(k)
                                    if (otherChild is RelativeLayout) {
                                        for (l in 0 until otherChild.childCount) {
                                            val otherTextView = otherChild.getChildAt(l)
                                            if (otherTextView is ToggleButton && otherTextView != buttonView) {
                                                otherTextView.isChecked = false
                                                otherTextView.background = defaultBackground
                                            }
                                        }
                                    }
                                }
                            } else {
                                textView.background = defaultBackground
                            }
                        }
                    }
                }
            }
        }
    }

    private fun reinitButtonState(gridLayout: GridLayout){
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            if (child is RelativeLayout) {
                for(j in 0 until child.childCount){
                    val textView = child.getChildAt(j)
                    if(textView is ToggleButton) {
                        if(textView.isChecked){
                            textView.isChecked = false
                            textView.background = defaultBackground
                        }
                    }
                }
            }
        }
    }
    private fun setNavButton(){
        val navButton: Button = binding.button
        navButton.setOnClickListener{
            val gridLayout = binding.includedLayout.root.findViewById<GridLayout>(R.id.mood_gridlayout)
            var isChecked = false // 标记是否至少有一个 ToggleButton 被选中

            for (i in 0 until gridLayout.childCount) {
                val child = gridLayout.getChildAt(i)
                if (child is RelativeLayout) {
                    for (j in 0 until child.childCount) {
                        val textView = child.getChildAt(j)
                        if (textView is ToggleButton && textView.isChecked) {
                            isChecked = true
                            break
                        }
                    }
                }
                if (isChecked) {
                    break
                }
            }

            if (!isChecked) {
                Toast.makeText(requireContext(), "请选择一个心情～", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val navController = findNavController()
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)   // 设置进入动画
                .setExitAnim(R.anim.slide_out_left)   // 设置退出动画
                .setPopEnterAnim(R.anim.slide_in_left)   // 设置返回动画
                .setPopExitAnim(R.anim.slide_out_right)   // 设置返回退出动画
                .build()
            navController.navigate(R.id.nav_event, null, navOptions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 解除绑定
        _binding = null
    }
}