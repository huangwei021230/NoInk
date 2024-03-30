package com.bagel.noink.ui.account

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bagel.noink.R
import com.bagel.noink.databinding.FragmentEditInformationBinding
import com.bagel.noink.ui.NoBottomTabFragment
import com.bagel.noink.utils.Contants
import com.bagel.noink.utils.UserHttpRequest
import org.json.JSONObject


class EditInformationFragment: NoBottomTabFragment() {
    private var _binding: FragmentEditInformationBinding? = null
    private val binding get() = _binding!!

    // 组件对象
    var updateEditText: EditText? = null
    var usernameMsg: TextView? = null
    var wechatIdText: EditText? = null
    var originPasswordEditText: EditText? = null
    var newPasswordEditText: EditText? = null
    var newPasswordAgainEditText: EditText? = null
    var passwordRepErrMsg: TextView? = null
    var saveButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val editInformationViewModel =
            ViewModelProvider(this).get(EditInformationViewModel::class.java)
        _binding = FragmentEditInformationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 绑定组件对象
        updateEditText = binding.updateEditText
        wechatIdText = binding.wechatIdText
        originPasswordEditText = binding.originPassword
        newPasswordEditText = binding.newPassword
        newPasswordAgainEditText = binding.newPasswordAgain
        passwordRepErrMsg = binding.passwordRepErrMsg
        saveButton = binding.saveButton
        usernameMsg = binding.usernameMsg

        // TODO: 获取参数
        val type = arguments?.getString("type")

        // 先默认全部不显示
        updateEditText?.visibility = View.GONE
        wechatIdText?.visibility = View.GONE
        originPasswordEditText?.visibility = View.GONE
        newPasswordEditText?.visibility = View.GONE
        newPasswordAgainEditText?.visibility = View.GONE

        // 调整显示的UI
        if (type.equals("username")) {
            updateEditText?.hint = Contants.UPDATE_USERNAME_PROMPT
            updateEditText?.visibility = View.VISIBLE
        } else if (type.equals("wechat")) {
            wechatIdText?.hint = Contants.UPDATE_WECHATID_PROMPT
            wechatIdText?.visibility = View.VISIBLE
        } else if (type.equals("password")) {
            originPasswordEditText?.visibility = View.VISIBLE
            newPasswordEditText?.visibility = View.VISIBLE
            newPasswordAgainEditText?.visibility = View.VISIBLE
        } else {
            throw Exception("error type message")
        }

        // 验证用户名是否重复
        updateEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed
            }

            override fun afterTextChanged(s: Editable?) {
                val userHttpRequest = UserHttpRequest()
                val username = updateEditText?.text.toString().trim { it <= ' ' }
                userHttpRequest.isUsernameExist(username) {
                    if (it) {
                        if (username != "") {
                            usernameMsg?.visibility = View.VISIBLE
                        } else {
                            usernameMsg?.visibility = View.GONE
                        }
                        usernameMsg?.text = "好听的用户名已经被人取走了"
                        usernameMsg?.setTextColor(Color.RED)
                        val drawable =
                            ContextCompat.getDrawable(requireContext(), R.drawable.lonely)
                        usernameMsg?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            drawable,
                            null,
                            null,
                            null
                        )
                        editInformationViewModel.usernameExist = true
                    } else {
                        if (username != "") {
                            usernameMsg?.visibility = View.VISIBLE
                        } else {
                            usernameMsg?.visibility = View.GONE
                        }
                        usernameMsg?.text = Contants.GOOD_USERNAME_PROMPT
                        usernameMsg?.setTextColor(Color.GREEN)
                        val drawable =
                            ContextCompat.getDrawable(requireContext(), R.drawable.happy)
                        usernameMsg?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            drawable,
                            null,
                            null,
                            null
                        )
                        editInformationViewModel.usernameExist = false
                    }
                }
            }
        })

        // 验证两次输入的密码是否一致
        newPasswordAgainEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed
            }
        })

        // 点击保存按钮
        saveButton!!.setOnClickListener {
            if (type.equals("username") && !editInformationViewModel.usernameExist) {
                val username = updateEditText?.text.toString().trim { it <= ' ' }
                AccountViewModel.updateUsername(username)
            }

            if (type.equals("wechat")) {
                val wechatId = wechatIdText?.text.toString().trim { it <= ' ' }
                AccountViewModel.updateWechatId(wechatId)
            }
            val userHttpRequest = UserHttpRequest()
            userHttpRequest.updateRequest(callbackListener = object :
                UserHttpRequest.UserCallbackListener {
                override fun onSuccess(responseJson: JSONObject) {
                    Log.v("USER", "Success to update user info.")
                }

                override fun onFailure(errorMessage: String) {
                    Log.v("NETWORK", "Fail to update user info.")
                }
            })

            requireActivity().supportFragmentManager.popBackStack()
        }

        return root
    }

    private fun validatePassword() {
        val password = newPasswordEditText?.text.toString().trim { it <= ' ' }
        val confirmPassword = newPasswordAgainEditText?.text.toString().trim { it <= ' ' }

        if (password == confirmPassword) {
            passwordRepErrMsg?.visibility = View.GONE
        } else {
            passwordRepErrMsg?.visibility = View.VISIBLE
            passwordRepErrMsg?.text = "两次输入的密码不一致"
        }
    }
}