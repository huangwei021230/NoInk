package com.bagel.noink.bean

import android.net.Uri

data class TextGenInfoBean(
    var imageUrls: List<Uri>?,
    var originText: String?,
    var style: String?,
    var type: String?,
    var length: String?,
    var taskId: String?
)