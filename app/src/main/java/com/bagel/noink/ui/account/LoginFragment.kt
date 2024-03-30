package com.bagel.noink.ui.account

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bagel.noink.R
import com.bagel.noink.activity.RegisterActivity
import com.bagel.noink.databinding.FragmentLoginBinding
import com.bagel.noink.ui.NoBottomTabFragment
import com.bagel.noink.utils.UserHttpRequest
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginFragment : NoBottomTabFragment() {
    private var _binding: FragmentLoginBinding? = null

    // 声明组件对象
    private var usernameText: EditText? = null
    private var passwdText: EditText? = null
    private var loginButton: Button? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
//        requireActivity().findViewById<Toolbar>(R.id.toolbar)?.visibility = View.GONE
//        setHasOptionsMenu(true)
//        requireActivity().actionBar?.hide()
        val loginViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 绑定组件对象
        usernameText = binding.username
        passwdText = binding.Password
        loginButton = binding.loginButton

        // 按下登录按钮
        loginButton?.setOnClickListener {
            val username = usernameText!!.text.toString()
            val passwd = passwdText!!.text.toString()
            val userHttpRequest = UserHttpRequest()
            userHttpRequest.loginRequest(
                username = username,
                password = passwd,
                callbackListener = object : UserHttpRequest.UserCallbackListener {
                    @SuppressLint("ShowToast")
                    override fun onSuccess(responseJson: JSONObject) {
                        // 返回400，用户名或密码不正确，弹出弹窗提示
                        if (responseJson.getInt("code") == 400) {
                            lifecycleScope.launch {
                                Toast.makeText(requireContext(), "用户名或密码不正确", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            return
                        }

                        val data = responseJson.getJSONObject("data")
                        lifecycleScope.launch {
                            // 在主线程中安全地更新 ViewModel
                            AccountViewModel.updateUserInfoByJson(data)
                            // 写入token
                            val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences?.edit()
                            editor?.putString("token", AccountViewModel.token)
                            editor?.apply()

                            // 登录成功，返回
                            requireActivity().onBackPressed()
                        }
                    }

                    override fun onFailure(errorMessage: String) {
                        // Nothing
                    }
                }
            )
        }

        // 跳转到注册
        binding.registerPrompt.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)   // 设置进入动画
                .setExitAnim(R.anim.slide_out_left)   // 设置退出动画
                .setPopEnterAnim(R.anim.slide_in_left)   // 设置返回动画
                .setPopExitAnim(R.anim.slide_out_right)   // 设置返回退出动画
                .build()

            findNavController().navigate(
                R.id.action_nav_login_to_nav_register,
                null,
                navOptions
            )
        }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}