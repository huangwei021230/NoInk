package com.bagel.noink.bean
import android.net.Uri
data class RecordCardBean(
    var id: Int,
    var date: String,
    var title: String,
    var photo: Uri,
    var content: String,
    var images: List<Uri>
)
