package com.bagel.noink.ui.account

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bagel.noink.R
import com.bagel.noink.activity.CommunityActivity
import com.bagel.noink.activity.HistoryActivity
import com.bagel.noink.activity.LoginActivity
import com.bagel.noink.activity.SearchActivity
import com.bagel.noink.databinding.FragmentAccountBinding
import com.bagel.noink.utils.UserHttpRequest
import com.bumptech.glide.Glide
import org.json.JSONObject

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var itemUsername: PersonalItemView? = null
    var itemWechatId: PersonalItemView? = null
    var itemGender: PersonalItemView? = null
    var itemBirthday: PersonalItemView? = null
    var itemUid: PersonalItemView? = null
    var itemUpdatePassword: PersonalItemView? = null
    var recordNumText: TextView? = null
    var articleNumText: TextView? = null
    var topUsername: TextView? = null
    var newGender: Boolean = true
    var newBirthday: String? = null
    private var avatar: Uri? = null
    val userHttpRequest = UserHttpRequest()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)
        AccountViewModel.instance = slideshowViewModel
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (avatar == null) {
            avatar = Uri.parse(AccountViewModel.userInfo?.avatar)
        }

        Glide.with(this@AccountFragment)
            .load(avatar)
            .into(binding.avatarImageView)

        // 实时更新用户信息显示UI
        slideshowViewModel._username.observe(viewLifecycleOwner) { newData ->
            itemUsername?.setData(newData)
            topUsername?.text = AccountViewModel.userInfo?.username
        }
        slideshowViewModel._wechatId.observe(viewLifecycleOwner) { newData ->
            itemWechatId?.setData(newData)
        }
        slideshowViewModel._birthday.observe(viewLifecycleOwner) { newData ->
            itemBirthday?.setData(newData)
        }
        slideshowViewModel._gender.observe(viewLifecycleOwner) { newData ->
            itemGender?.setData(if (newData) "男" else "女")
        }

        slideshowViewModel._articleNum.observe(viewLifecycleOwner) { newData ->
            articleNumText?.text = newData.toString()
        }

        slideshowViewModel._recordNum.observe(viewLifecycleOwner) { newData ->
            recordNumText?.text = newData.toString()
        }

        slideshowViewModel._avatar.observe(viewLifecycleOwner) { newData ->
            Glide.with(this@AccountFragment)
                .load(newData)
                .into(binding.avatarImageView)
        }

        binding.testButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_personal_account_to_nav_login)
        }

        binding.recordContainer.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.postContainer.setOnClickListener {
            val intent = Intent(requireContext(), CommunityActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 绑定相关组件
        itemUsername = activity?.findViewById(R.id.item_username)
        itemWechatId = activity?.findViewById(R.id.item_wechat)
        itemGender = activity?.findViewById(R.id.item_gender)
        itemBirthday = activity?.findViewById(R.id.item_birthday)
        itemUid = activity?.findViewById(R.id.item_uid)
        itemUpdatePassword = activity?.findViewById(R.id.item_update_password)
        recordNumText = activity?.findViewById(R.id.record_num)
        articleNumText = activity?.findViewById(R.id.article_num)
        topUsername = activity?.findViewById(R.id.top_username)

        // 显示用户信息
        AccountViewModel.userInfo?.let { itemUsername?.setData(it.username) }
        AccountViewModel.userInfo?.let { itemWechatId?.setData(it.wechatId) }
        AccountViewModel.userInfo?.let {
            itemGender?.setData(
                if (it.gender) "男" else "女"
            )
        }
        AccountViewModel.userInfo?.let { itemBirthday?.setData(it.birthday) }
        AccountViewModel.userInfo?.let { itemUid?.setData(it.id.toString()) }
        recordNumText?.text = AccountViewModel.userInfo?.recordNum.toString()
        articleNumText?.text = AccountViewModel.userInfo?.articleNum.toString()
        topUsername?.text = AccountViewModel.userInfo?.username

        // 跳转到修改用户名界面
        itemUsername?.setOnClickListener { slideNavToEditInfo("username") }

        // 跳转到修改微信账号界面
        itemWechatId?.setOnClickListener { slideNavToEditInfo("wechat") }

        // 弹出修改性别弹窗
        itemGender?.setOnClickListener {
            val genderOptions = arrayOf("男", "女")
            val checkedItem = 0
            var tempGender = true
            val builder = AlertDialog.Builder(this.activity)
            builder.setTitle("修改性别")
            builder.setSingleChoiceItems(genderOptions, checkedItem) { dialog, which ->
                // 处理选中的性别
                val selectedGender = genderOptions[which]
                tempGender = selectedGender == "男"
            }

            // 点击 "确定" 按钮关闭对话框
            builder.setPositiveButton("确定") { dialog, _ ->
                newGender = tempGender
                AccountViewModel.updateGender(newGender)
                userHttpRequest.updateRequest(callbackListener = object :
                    UserHttpRequest.UserCallbackListener {
                    override fun onSuccess(responseJson: JSONObject) {
                        Log.v("USER", "Success to update user info.")
                    }

                    override fun onFailure(errorMessage: String) {
                        Log.v("NETWORK", "Fail to update user info.")
                    }
                })
                dialog.dismiss()
            }

            // 点击 "取消" 按钮关闭对话框
            builder.setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        // 修改生日弹窗
        itemBirthday?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    // 处理用户选择的日期
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth + 1, selectedDay)

                    newBirthday = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    AccountViewModel.updateBirthday(newBirthday!!)
                    userHttpRequest.updateRequest(callbackListener = object :
                        UserHttpRequest.UserCallbackListener {
                        override fun onSuccess(responseJson: JSONObject) {
                            Log.v("USER", "Success to update user info.")
                        }

                        override fun onFailure(errorMessage: String) {
                            Log.v("NETWORK", "Fail to update user info.")
                        }
                    })
                }, year, month, day)

            // 设置最大日期
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            // 显示日期选择对话框
            datePickerDialog.show()
        }


        // 跳转到修改用户密码界面
        itemUpdatePassword?.setOnClickListener { slideNavToEditInfo("password") }

        activity?.findViewById<Button>(R.id.exitButton)
            ?.setOnClickListener {
                // 清除 token
                val sharedPreferences =
                    activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()
                editor?.putString("token", "")
                editor?.apply()

                AccountViewModel.needToUpdateHistory = true

                // 跳转到LoginActivity
                val intent = Intent(this.context, LoginActivity::class.java)
                startActivity(intent)
            }
    }

    private fun slideNavToEditInfo(type: String) {
        val bundle = bundleOf(
            "type" to type
        )

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)   // 设置进入动画
            .setExitAnim(R.anim.slide_out_left)   // 设置退出动画
            .setPopEnterAnim(R.anim.slide_in_left)   // 设置返回动画
            .setPopExitAnim(R.anim.slide_out_right)   // 设置返回退出动画
            .build()

        findNavController().navigate(
            R.id.action_nav_personal_account_to_nav_edit_information,
            bundle,
            navOptions
        )
    }
}