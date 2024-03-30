package com.bagel.noink.ui.account

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bagel.noink.bean.RecordCardBean
import com.bagel.noink.bean.UserInfoBean
import org.json.JSONObject

class AccountViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text

    val _userInformation = MutableLiveData<UserInfoBean>()
    val _username = MutableLiveData<String>()
    val _gender = MutableLiveData<Boolean>()
    val _birthday = MutableLiveData<String>()
    val _wechatId = MutableLiveData<String>()
    val _uid = MutableLiveData<Long>()
    val _recordNum = MutableLiveData<Int>()
    val _articleNum = MutableLiveData<Int>()
    val _avatar = MutableLiveData<String>()

    companion object {
        var token: String? = ""
        var instance: AccountViewModel? = null

        var needToUpdateHistory = true
        // 用户信息
        var userInfo: UserInfoBean? = UserInfoBean(
            1,
            "测试用户",
            "testPassword123",
            true,
            18,
            "13989884399",
            "2005-8-31",
            Uri.parse(""),
            0,
            0,
            ""
        )

        var cardList: MutableList<RecordCardBean> = ArrayList()

        fun updateUserInfoByJson(data: JSONObject) {
            userInfo?.id = data.getLong("uid")
            userInfo?.age = data.getInt("age")
            userInfo?.gender = data.getBoolean("gender")
            userInfo?.username = data.getString("username")
            userInfo?.password = data.getString("password")
            userInfo?.birthday = data.getString("birthday")
            userInfo?.wechatId = data.getString("wechatId")
            userInfo?.userprofile = Uri.parse(data.getString("userprofile"))
            userInfo?.recordNum = data.getInt("recordNum")
            userInfo?.articleNum = data.getInt("articleNum")
            userInfo?.avatar = data.getString("userprofile")
            if (data.getString("tokenValue") != "null") {
                token = data.getString("tokenValue")
            }

            if (instance != null) {
                instance!!._userInformation.value = userInfo
                instance!!._username.value = userInfo?.username
                instance!!._gender.value = userInfo?.gender
                instance!!._birthday.value = userInfo?.birthday
                instance!!._wechatId.value = userInfo?.wechatId
                instance!!._uid.value = userInfo?.id
                instance!!._recordNum.value = userInfo?.recordNum
                instance!!._articleNum.value = userInfo?.articleNum
                instance!!._avatar.value = userInfo?.avatar
            }
        }

        fun updateUserInfo(newInfo: UserInfoBean) {
            userInfo = newInfo
            if (instance != null) {
                instance!!._userInformation.value = userInfo
            }
        }

        fun updateGender(newGender: Boolean) {
            userInfo?.gender = newGender
            if (instance != null) {
                instance!!._userInformation.value?.gender = newGender
                instance!!._gender.value = newGender
            }
        }

        fun updateBirthday(newBirthday: String) {
            userInfo?.birthday = newBirthday
            if (instance != null) {
                instance!!._userInformation.value?.birthday = newBirthday
                instance!!._birthday.value = newBirthday
            }
        }

        fun updateUsername(newUsername: String) {
            userInfo?.username = newUsername
            if (instance != null) {
                instance!!._userInformation.value?.username = newUsername
                instance!!._username.value = newUsername
            }
        }

        fun updateWechatId(newWechatId: String) {
            userInfo?.wechatId = newWechatId
            if (instance != null) {
                instance!!._userInformation.value?.wechatId = newWechatId
                instance!!._wechatId.value = newWechatId
            }
        }

        fun saveToken(activity: AppCompatActivity) {
            val sharedPreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("token", token)
            editor.apply()
        }
    }

}