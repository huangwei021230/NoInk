package com.bagel.noink.ui.account

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bagel.noink.R
import com.bagel.noink.databinding.FragmentLoginBinding
import com.bagel.noink.databinding.FragmentRegisterBinding
import com.bagel.noink.ui.NoBottomTabFragment
import com.bagel.noink.utils.Contants
import com.bagel.noink.utils.InformationCalc
import com.bagel.noink.utils.UserHttpRequest
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterFragment: Fragment() {
    private var _binding: FragmentRegisterBinding? = null

    // 声明组件
    var usernameText: EditText? = null
    var passwdText: EditText? = null
    var passwdText2: EditText? = null
    var wechatId: EditText? = null
    var registerButton: Button? = null
    var birthdayText: EditText? = null
    var passwordRepErrMsg: TextView? = null
    var backToLoginPrompt: TextView? = null
    var usernameMsg: TextView? = null

    // 有用的信息
    var birthday: String = ""
    var usernameExist: Boolean = false

    private val binding get() = _binding!!
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val registerViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 绑定组件对象
        usernameText = binding.username
        passwdText = binding.Password
        passwdText2 = binding.Password2
        wechatId = binding.wechatId
        registerButton = binding.registerButton
        birthdayText = binding.editTextBirthday
        passwordRepErrMsg = binding.passwordRepErrMsg
        backToLoginPrompt = binding.backToLoginPrompt
        usernameMsg = binding.usernameMsg
        val genderRadioGroup: RadioGroup = binding.radioGroup

        // 从单选框中获得性别信息
        var gender = true
        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radbtn = view?.findViewById<View>(checkedId) as RadioButton
            gender = radbtn.text.equals("男")
        }

        // 验证用户名是否重复
        usernameText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed
            }

            override fun afterTextChanged(s: Editable?) {
                val userHttpRequest = UserHttpRequest()
                val username = usernameText?.text.toString().trim { it <= ' ' }
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
                        usernameExist = true
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
                        usernameExist = false
                    }
                }
            }
        })


        // 验证两次输入的密码是否一致
        passwdText2?.addTextChangedListener(object : TextWatcher {
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

        birthdayText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    // 处理用户选择的日期
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth + 1, selectedDay)
                    birthday = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    birthdayText!!.setText("$selectedYear.${selectedMonth + 1}.$selectedDay")
                }, year, month, day)
            // 设置最大日期
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            // 显示日期选择对话框
            datePickerDialog.show()
        }

        // 点击注册按钮
        registerButton!!.setOnClickListener {
            // 去除空格
            val name = usernameText?.text.toString().trim { it <= ' ' }
            val passwd = passwdText?.text.toString().trim { it <= ' ' }
            val passwd2 = passwdText2?.text.toString().trim { it <= ' ' }
            val wechatId = wechatId?.text.toString().trim { it <= ' ' }
            val birthday = birthdayText?.text.toString().trim { it <= ' ' }
            val userHttpRequest = UserHttpRequest()

            if (passwd == passwd2 && !usernameExist
                && name != ""
                && wechatId != ""
                && birthday != ""
            ) {
                // 计算年龄
                val age = InformationCalc.calculateAge(birthday)
                // 调用sendTextRequest方法发送请求
                userHttpRequest.registerRequest(
                    username = name,
                    password = passwd,
                    gender = gender,
                    age = age,
                    wechatId = wechatId,
                    birthday = InformationCalc.convertDateFormat(birthday),
                    callbackListener = object : UserHttpRequest.UserCallbackListener {
                        override fun onSuccess(responseJson: JSONObject) {
                            // TODO: 修改成导航到上一级页面
                            // finish()
                        }

                        override fun onFailure(errorMessage: String) {
                            println("Failure")
                        }
                    }
                )
            }
        }

        // 返回登录页面
        backToLoginPrompt!!.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return root
    }
    private fun validatePassword() {
        val password = passwdText?.text.toString().trim { it <= ' ' }
        val confirmPassword = passwdText2?.text.toString().trim { it <= ' ' }

        if (password == confirmPassword) {
            passwordRepErrMsg?.visibility = View.GONE
        } else {
            passwordRepErrMsg?.visibility = View.VISIBLE
            passwordRepErrMsg?.text = "两次输入的密码不一致"
        }
    }
}