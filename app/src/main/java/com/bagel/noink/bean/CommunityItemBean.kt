package com.bagel.noink.bean

import android.net.Uri

data class CommunityItemBean(
    var aid: Int, // 文章id
    var title: String,
    var avatar: Uri,  // 发布者头像
    var createdAt: String,
    var updatedAt: String,
    var content: String,
    var imageUrls: List<Uri>?,
    var moods: String,
    var events: String,
    var pv: Int,  // 浏览量
    var likes: Int,  // 点赞个数
    var comments: Int,
    var state: Int, // 为web管理端暂存
    var uid: Int, // 发布uid
    var username: String,
    var commentList: List<CommentItemBean>?
)
