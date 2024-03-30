package com.bagel.noink.bean

import android.net.Uri

data class CommentItemBean (
    var cid: Int,
    var pid: Int,
    var createAt: String,
    var updateAt: String,
    var content: String,
    var aid: Int,
    var state: Int,
    var commentUser: Int,
    var username: String,
    var likes: Int,
    var commentList: List<CommentItemBean>?,
    var avatar: Uri
)