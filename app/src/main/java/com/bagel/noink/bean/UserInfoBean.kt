package com.bagel.noink.bean

import android.net.Uri

data class UserInfoBean(
    var id : Long,
    var username: String,
    var password: String,
    var gender: Boolean,
    var age: Int,
    var wechatId: String,
    var birthday: String,
    var userprofile: Uri,
    var recordNum: Int,
    var articleNum: Int,
    var avatar: String
)
